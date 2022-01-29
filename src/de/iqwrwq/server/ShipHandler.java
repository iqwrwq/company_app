package de.iqwrwq.server;

import de.iqwrwq.client.Cargo;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.ui.Command;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

public abstract class ShipHandler extends Thread {


    public CommunicationHandler communicationHandler;
    protected Socket socket;
    protected Kernel core;

    public ShipHandler(Socket socket, Kernel core) {
        this.socket = socket;
        this.core = core;
    }

    @Override
    public void run() {
        try {
            shipCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shipCommunication() throws IOException {
        while (!socket.isClosed()) {
            handleRequest();
        }
    }

    private void handleRequest() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Command shipRequest;

        while (bufferedReader.ready()) {
            String[] parts = bufferedReader.readLine().split(Pattern.quote(" "));
            shipRequest = new Command(parts[0], parts);

            switch (shipRequest.header) {
                case "register" -> registerShip(shipRequest);
                case "charge" -> chargeCompany(shipRequest);
                case "loaded" -> setCargo(shipRequest);
                case "remove" -> removeShip();
                default -> {}
            }
        }
    }

    public abstract void registerShip(Command registerShip);

    public abstract void chargeCompany(Command chargeCompany);

    public abstract void setCargo(Command setCargo);

    public abstract void removeShip() throws IOException;



}
