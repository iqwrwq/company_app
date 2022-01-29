package de.iqwrwq.server;

import de.iqwrwq.client.Cargo;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public class ShipServer extends Server {

    private static final String INSTANCE_NAME = "ShipServer";

    volatile public ArrayList<Cargo> cargos = new ArrayList<Cargo>();
    public final HashMap<Integer, ShipThread> shipConnectionMap = new HashMap<>();

    private final Kernel core;
    private final int maxShips;
    private final Harbours harbours;
    private CommunicationHandler communicationHandler;
    private int currentShips;

    public ShipServer(Kernel core) {
        super(core.config);

        this.core = core;
        this.maxShips = core.config.maxShips;
        this.currentShips = 0;
        this.harbours = new Harbours(core.config);
    }

    protected void process() throws IOException {
        while (!serverSocket.isClosed()) {
            handleUpcomingShipConnections(serverSocket);
        }
    }

    protected void connect() throws IOException {
        this.serverSocket = new ServerSocket(port);
        CommunicationHandler.forceMessage(INSTANCE_NAME, "Started" + req.DIVIDER + "Port" + req.SEPARATOR + port);
    }

    public void move(int id, String to) {
        if (harbours.contains(to)) {
            shipConnectionMap.get(id).communicationHandler.notifyServer("move" + req.SEPARATOR + to);
        } else {
            System.out.println("Error" + req.DIVIDER + "NoSuchHarbour");
        }
    }

    public void load(String cmd) {
        String[] cmd_parts = cmd.split(Pattern.quote(" "));
        ShipThread targetShip = shipConnectionMap.get(Integer.parseInt(cmd_parts[1]));
        if (cmd_parts.length >= 3) {
            targetShip.communicationHandler.notifyServer("load" + req.DIVIDER + cmd_parts[2]);
        } else {
            targetShip.communicationHandler.notifyServer("load");
        }
    }

    public Object getHarbour() {
        int index = (int) (Math.random() * harbours.size());
        return this.harbours.get(index);
    }

    public void exit() {
        try {
            shipConnectionMap.forEach((id, ship) -> {
                ship.communicationHandler.notifyAll("removed");
            });
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUpcomingShipConnections(ServerSocket serverSocket) throws IOException {
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

    private void startShipThread(Socket shipSocket, CommunicationHandler communicationHandler) {
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
