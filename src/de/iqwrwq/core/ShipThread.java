package de.iqwrwq.core;

import java.io.*;
import java.net.Socket;

public class ShipThread extends Thread {

    public CommunicationHandler communicationHandler;
    public String harbour;
    public Cargo cargo;
    private final int id;
    private final Socket socket;
    private final Core core;

    public ShipThread(int id, Socket socket, Core core) {
        this.id = id;
        this.socket = socket;
        this.core = core;
        try {
            this.communicationHandler = new CommunicationHandler("Ship" + req.DIVIDER + id, new PrintWriter(socket.getOutputStream(), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            shipCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInfo(){
        return "Ship" + req.DIVIDER + id + req.DIVIDER + harbour;
    }

    private void shipCommunication() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String shipRequest;
        while (!socket.isClosed()) {
            while (bufferedReader.ready()) {
                shipRequest = bufferedReader.readLine();
                System.out.println("Ship | " + id + " -> " + shipRequest);
                switch (shipRequest.split(" -> ")[0]) {
                    case "register" -> {
                        this.harbour = core.shipServer.getHarbour();
                        communicationHandler.notifyServer("registered" + req.SEPARATOR + id + req.DIVIDER + harbour + req.DIVIDER + core.company.companyName);
                    }
                    case "charge" -> {
                        core.company.setEstate(-Integer.parseInt(shipRequest.split(" -> ")[1]));
                    }
                    case "loaded" -> {
                        this.cargo = new Cargo(shipRequest.split(" -> ")[1]);
                    }
                    case "remove" -> {
                        removeShip();
                    }
                    default -> {
                    }
                }
            }
        }
        System.out.println("ShipServer -> ThreadClosed | " + id);
    }

    private void removeShip() throws IOException {
        System.out.println("ShipServer -> removed -> " + id);
        communicationHandler.notifyServer("removed -> " + id);
        socket.close();
    }
}
