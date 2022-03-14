package de.iqwrwq.server;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.server.entities.Cargo;
import de.iqwrwq.server.entities.Harbour;
import de.iqwrwq.ui.Command;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ShipThread extends ShipHandler {

    public Harbour harbour;
    public Cargo cargo;
    public final int id;
    public CommunicationHandler communicationHandler;
    public boolean isMoving;

    public ShipThread(int id, @NotNull Socket socket, Kernel core) {
        super(socket, core);
        this.id = id;
        try {
            this.communicationHandler = new CommunicationHandler("Ship" + req.DIVIDER + id, new PrintWriter(socket.getOutputStream(), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isMoving = false;
    }

    @Override
    public void registerShip(Command registerShip) {
        Harbour harbour = core.shipServer.getHarbour();
        harbour.ships.add(this);
        this.harbour = harbour;
        communicationHandler.notifyServer("registered" + req.SEPARATOR + id + req.DIVIDER + harbour.name + req.DIVIDER + core.company.companyName);
    }

    @Override
    public void chargeCompany(@NotNull Command chargeCompany) {
        int chargeIndex = 2;
        core.company.setEstate(Integer.parseInt(chargeCompany.arguments.get(chargeIndex)));
    }

    @Override
    public void setCargo(@NotNull Command setCargo) {
        int cargoIndex = 2;
        int cargoSpaceIndex = 3;
        String cargoInfo = setCargo.arguments.size() == 4 ? setCargo.arguments.get(cargoIndex) + " " + setCargo.arguments.get(cargoSpaceIndex) : setCargo.arguments.get(cargoIndex);

        this.cargo = new Cargo(cargoInfo);
        core.shipServer.cargos.removeIf(cargo -> cargo.id == this.cargo.id);
        core.shipServer.harbours.forEach((harbour) -> {
            if (harbour.name.equals(cargo.source.name)) {
                harbour.cargos.removeIf((cargo1) -> cargo1.id == cargo.id);
            }
        });

        communicationHandler.notifyApp("loadedCargo" + req.SEPARATOR + cargo.getObjString());
    }

    @Override
    public void unloadCargo() {
        this.cargo = null;
    }

    @Override
    public void removeShip() {
        try {
            communicationHandler.notifyAll("removed");
            socket.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setHarbour(Command setHarbour) {
        int destinationHarbourIndex = 2;
        int destinationHarbourSpaceIndex = 3;
        ArrayList<String> command = setHarbour.arguments;

        harbour.ships.remove(this);
        isMoving = false;

        for (Harbour harbour : core.shipServer.harbours) {

            if (harbour.name.equals(command.size() == 4 ? command.get(destinationHarbourIndex) + " " + command.get(destinationHarbourSpaceIndex) : command.get(destinationHarbourIndex))) {
                this.harbour = harbour;
                harbour.ships.add(this);
            }
        }
        communicationHandler.notifyApp("reachedAndSetHarbour" + req.SEPARATOR + harbour.name);
    }

    public void setIsMoving() {
        isMoving = true;
    }

    @Override
    public void notifyError(Command error) {
        int errorTextIndex = 1;

        communicationHandler.notifyApp("Error" + req.SEPARATOR + error.arguments.get(errorTextIndex), "\u001B[31m");
    }

    @Override
    public @NotNull String[] getInfo() {
        return new String[]{
                "Ship",
                String.valueOf(id),
                (cargo == null ?
                        "no Cargo" :
                        cargo.getObjString()
                ),
                harbour.name
        };
    }
}
