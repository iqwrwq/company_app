package de.iqwrwq.client;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.entities.Cargo;
import de.iqwrwq.server.entities.Harbour;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Company extends Client {


    public String companyName;
    public int estate;
    public CommunicationHandler communicationHandler;

    public Company(@NotNull Kernel core) {
        super(core);
        this.companyName = core.config.companyName;
    }

    @Override
    protected void process() throws IOException {
        while (!toServerSocket.isClosed()) {
            readAndSendSeaTradeRequest();
        }
    }

    private void readAndSendSeaTradeRequest() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(toServerSocket.getInputStream()));
        String serverAnswer;

        while (bufferedReader.ready()) {
            serverAnswer = bufferedReader.readLine();
            if (!core.config.muteSync) communicationHandler.notifyApp(serverAnswer);
            handle(serverAnswer);
        }
    }

    @Override
    protected void handle(@NotNull String request) {
        switch (request.split(":")[0]) {
            case "registered" -> {
                setEstate(Integer.parseInt(request.split(":")[2]));
                communicationHandler.notifyApp("registeredOnSeaTrade" + req.DIVIDER + "Port" + req.SEPARATOR + core.config.port, "\u001B[32m");
                if (core.config.initialAutoSync) {
                    communicationHandler.notifyServer("getinfo:cargo");
                    communicationHandler.notifyApp("SyncedAllCargos", "\u001B[32m");
                }
            }
            case "newCargo" -> {
                Cargo cargo = new Cargo(request);
                addCargo(cargo);
                for (Harbour harbour : core.shipServer.harbours) {
                    if (harbour.name.equals(cargo.source.name)) {
                        harbour.cargos.add(cargo);
                    }
                }
            }
            case "cargo" -> listCargos(request);
        }
    }

    private void listCargos(@NotNull String request) {
        Cargo suspectedNewCargo = new Cargo(request);
        if (!core.shipServer.cargos.isEmpty()) {
            for (Cargo cargo : core.shipServer.cargos) {
                if (cargo.id == suspectedNewCargo.id) {
                    return;
                }
            }
        }
        addCargo(suspectedNewCargo);
    }

    @Override
    protected void register() throws IOException {
        String randomCompany = randomCompany();

        this.communicationHandler = new CommunicationHandler(randomCompany, new PrintWriter(toServerSocket.getOutputStream(), true));
        communicationHandler.notifyApp("SeaTradeConnection" + req.DIVIDER + "Port" + req.SEPARATOR + core.config.port);
        communicationHandler.notifyServer("register:" + randomCompany);
        this.companyName = randomCompany;
    }

    @Override
    protected void reconnect() {
        try {
            Logger.warn("Reconnection");
            CommunicationHandler.forceMessage(companyName, "Error" + req.SEPARATOR + "ConnectionRefused");
            CommunicationHandler.forceMessage(companyName, "RetryAfterWait" + req.DIVIDER + core.config.getProperty("ReconnectionTimeGap"));
            sleep(Integer.parseInt(core.config.getProperty("ReconnectionTimeGap")));
            CommunicationHandler.forceMessage(companyName, "RetryConnection");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void setEstate(int amount) {
        this.estate += amount;
        System.out.println(companyName + req.DIVIDER + "setEstate" + req.DIVIDER + amount + req.SEPARATOR + estate);
    }

    public void exit() {
        try {
            if (toServerSocket != null) {
                communicationHandler.notifyAll("exit");
                this.toServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Logger.info("Company exited");
        }
    }

    private void addCargo(@NotNull Cargo suspectedNewCargo) {
        for (Harbour harbour : core.shipServer.harbours){
            if (harbour.name.equals(suspectedNewCargo.source.name)){
                harbour.cargos.add(suspectedNewCargo);
            }
        }
        core.shipServer.cargos.add(suspectedNewCargo);
    }

    /**
     * @return Random ship
     * @implSpec for Dev
     */
    private @NotNull String randomCompany() {
        return core.config.companyName + (int) (Math.random() * (999 - 1 + 1) + 1);
    }

}
