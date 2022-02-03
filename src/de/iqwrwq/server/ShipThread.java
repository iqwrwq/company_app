package de.iqwrwq.server;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.objects.Cargo;
import de.iqwrwq.server.objects.Harbour;
import de.iqwrwq.ui.Command;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ShipThread extends ShipHandler {

    public Harbour harbour;
    public Cargo cargo;
    private final int id;
    public CommunicationHandler communicationHandler;

    public ShipThread(int id, @NotNull Socket socket, Kernel core) {
        super(socket, core);
        this.id = id;
        try {
            this.communicationHandler = new CommunicationHandler("Ship" + req.DIVIDER + id, new PrintWriter(socket.getOutputStream(), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerShip(Command registerShip) {
        this.harbour = core.shipServer.getHarbour();
        communicationHandler.notifyServer("registered" + req.SEPARATOR + id + req.DIVIDER + harbour.name + req.DIVIDER + core.company.companyName);
    }

    @Override
    public void chargeCompany(@NotNull Command chargeCompany) {
        int chargeIndex = 2;
        core.company.setEstate(Integer.parseInt(chargeCompany.arguments.get(chargeIndex)));
    }

    @Override
    public void setCargo(@NotNull Command setCargo) {
        int cargoIndex = 1;
        this.cargo = new Cargo(setCargo.arguments.get(cargoIndex));
    }

    @Override
    public void removeShip() throws IOException {
        socket.close();
    }

    @Override
    public @NotNull String[] getInfo() {
        return new String[]{"Ship", String.valueOf(id), (cargo == null ? "no Cargo" : String.valueOf(cargo.id)), harbour.name};
    }
}
