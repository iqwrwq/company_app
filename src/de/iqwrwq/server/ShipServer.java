package de.iqwrwq.server;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.objects.Cargo;
import de.iqwrwq.server.objects.Harbour;
import de.iqwrwq.server.objects.Harbours;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public class ShipServer extends Server {

    public static final String INSTANCE_NAME = "ShipServer";

    volatile public @NotNull ArrayList<Cargo> cargos = new ArrayList<>();
    public final HashMap<Integer, ShipThread> shipConnectionMap = new HashMap<>();
    public final @NotNull Harbours harbours;

    private final @NotNull Kernel core;
    private final int maxShips;
    private int currentShips;

    public ShipServer(@NotNull Kernel core) {
        super(core.config);

        this.core = core;
        this.maxShips = core.config.maxShips;
        this.currentShips = 0;
        this.harbours = new Harbours(core.config);
    }

    @Override
    protected void process() throws IOException {
        while (!serverSocket.isClosed()) {
            handleUpcomingShipConnections(serverSocket);
        }
    }

    @Override
    protected void connect() throws IOException {
        this.serverSocket = new ServerSocket(port);
        CommunicationHandler.forceMessage(INSTANCE_NAME, "Started" + req.DIVIDER + "Port" + req.SEPARATOR + port, "\u001B[32m");
    }

    public void move(int id, String to) {
        for (Harbour harbour : harbours) {
            if (harbour.name.equals(to)) {
                shipConnectionMap.get(id).communicationHandler.notifyServer("move" + req.SEPARATOR + to);
                return;
            }
        }
        CommunicationHandler.forceMessage(INSTANCE_NAME, "Error" + req.SEPARATOR + "NoSuchHarbour", "\u001B[31m");
    }

    public void load(@NotNull String cmd) {
        String[] cmd_parts = cmd.split(Pattern.quote(" "));
        ShipThread targetShip = shipConnectionMap.get(Integer.parseInt(cmd_parts[1]));
        if (cmd_parts.length >= 3) {
            targetShip.communicationHandler.notifyServer("load" + req.DIVIDER + cmd_parts[2]);
        } else {
            targetShip.communicationHandler.notifyServer("load");
        }
    }

    public Harbour getHarbour() {
        int index = (int) (Math.random() * harbours.size());
        return this.harbours.get(index);
    }

    @Override
    public void exit() {
        try {
            shipConnectionMap.forEach((id, ship) -> ship.communicationHandler.notifyAll("removed"));
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Logger.info("ShipServer closed");
        }
    }

    private void handleUpcomingShipConnections(@NotNull ServerSocket serverSocket) throws IOException {
        try {
            Socket shipSocket = serverSocket.accept();
            CommunicationHandler communicationHandler = new CommunicationHandler(INSTANCE_NAME, new PrintWriter(shipSocket.getOutputStream(), true));

            if (maxShips > currentShips) {
                startShipThread(shipSocket, communicationHandler);
            } else {
                communicationHandler.notifyAll("Error" + req.SEPARATOR + "MaxShipsExceeded" + req.DIVIDER + maxShips);
            }
        } catch (SocketException ignored) {
        }
    }

    private void startShipThread(Socket shipSocket, @NotNull CommunicationHandler communicationHandler) {
        int shipId = generateId();
        currentShips++;

        communicationHandler.notifyApp("newShipConnection" + req.DIVIDER + shipId);
        ShipThread shipThread = new ShipThread(shipId, shipSocket, core);
        shipConnectionMap.put(shipId, shipThread);
        shipThread.start();
    }

    private int generateId() {
        return 1000 + (int) (Math.random() * (999 - 1 + 1) + 1);
    }
}
