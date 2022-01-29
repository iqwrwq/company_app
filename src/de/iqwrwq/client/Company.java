package de.iqwrwq.client;

import de.iqwrwq.config.Config;
import de.iqwrwq.core.Kernel;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Company extends Thread {

    private static final int DEFAULT_COMPANY_ESTATE = 0;

    public String companyName;
    protected Config config;
    private Socket seaTradeSocket;
    private int estate = DEFAULT_COMPANY_ESTATE;
    private CommunicationHandler communicationHandler;
    private final Kernel core;

    public Company(Kernel core) {
        this.core = core;
        this.config = core.config;
        this.companyName = config.companyName;
    }

    @Override
    public void run() {
        int allowedConnectionAttempts = Integer.parseInt(config.getProperty("ConnectionAttempts"));
        for (int connectionAttempt = 0; connectionAttempt <= allowedConnectionAttempts; connectionAttempt++) {
            try {
                this.seaTradeSocket = connectToSeaTradeServer();
                seaTradeCommunication();
                break;
            } catch (IOException e) {
                tryReconnection();
            } finally {
                communicationHandler.notifyApp("ApplicationEnded" + req.DIVIDER + "✔", "\u001B[31m");
            }
        }
    }

    public void sendRequestToSeaTrade(String req) {
        communicationHandler.notifyServer(req);
    }

    private void tryReconnection() {
        try {
            CommunicationHandler.forceMessage(companyName, "Error" + req.SEPARATOR + "ConnectionRefused");
            CommunicationHandler.forceMessage(companyName, "RetryAfterWait" + req.DIVIDER + config.getProperty("ReconnectionTimeGap"));
            sleep(Integer.parseInt(config.getProperty("ReconnectionTimeGap")));
            CommunicationHandler.forceMessage(companyName, "RetryConnection");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void setEstate(int amount) {
        this.estate += amount;
        System.out.println(companyName + req.DIVIDER + "estate" + req.DIVIDER + estate);
    }

    public void exit() {
        try {
            communicationHandler.notifyAll("exit");
            this.seaTradeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket connectToSeaTradeServer() throws IOException {
        Socket seaTradeSocket = new Socket(config.host, config.port);

        this.communicationHandler = new CommunicationHandler(companyName, new PrintWriter(seaTradeSocket.getOutputStream(), true));
        communicationHandler.notifyApp("SeaTradeConnection" + req.DIVIDER + "Port" + req.SEPARATOR + config.port);

        String rc = randomCompany();
        communicationHandler.notifyServer("register:" + rc);
        this.companyName = rc;
        //communicationHandler.notifyServer("register:" + config.companyName);

        return seaTradeSocket;
    }

    private void seaTradeCommunication() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(seaTradeSocket.getInputStream()));

        while (!seaTradeSocket.isClosed()) {
            handleSeaTradeRequest(bufferedReader);
        }
    }

    private void handleSeaTradeRequest(BufferedReader bufferedReader) throws IOException {
        String serverAnswer;

        while (bufferedReader.ready()) {
            serverAnswer = bufferedReader.readLine();
            communicationHandler.notifyApp(serverAnswer);

            switch (serverAnswer.split(":")[0]) {
                case "registered" -> {
                    setEstate(Integer.parseInt(serverAnswer.split(":")[2]));
                    communicationHandler.notifyApp("registeredOnSeaTrade");
                }
                case "newCargo" -> {
                    Cargo cargo = new Cargo(serverAnswer);
                    communicationHandler.notifyApp("addedCargo" + req.DIVIDER + cargo.id);
                    core.shipServer.cargos.add(cargo);
                }
                case "cargo" -> {
                    Cargo suspectedNewCargo = new Cargo(serverAnswer);
                    if (!core.shipServer.cargos.contains(suspectedNewCargo)) {
                        communicationHandler.notifyApp("addedUnknownCargo" + req.DIVIDER + suspectedNewCargo.id);
                        core.shipServer.cargos.add(suspectedNewCargo);
                    }
                }
                default -> {
                    communicationHandler.notifyApp("unhandledRequestAbove", "\u001B[33m");
                }
            }
        }
    }

    /**
     * to refactor after Dev
     */
    private String randomCompany() {
        return config.companyName + (int) (Math.random() * (999 - 1 + 1) + 1);
    }

}
