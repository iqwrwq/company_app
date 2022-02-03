package de.iqwrwq.server.objects;

import de.iqwrwq.server.ShipThread;
import de.iqwrwq.ui.req;

import java.util.ArrayList;

public class Harbour {
    public String name;
    public ArrayList<ShipThread> ships;
    public ArrayList<Cargo> cargos;

    public Harbour(String name) {
        this.name = name;
        this.ships = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    public String[] getInfo() {
        var Reference = new Object() {
            String shipInfos = "";
            String cargoInfo = "";
        };

        ships.forEach((ship) -> Reference.shipInfos += ship.id + " ");
        cargos.forEach((cargo) -> Reference.cargoInfo += cargo.getObjString());

        return new String[]{name, Reference.shipInfos, Reference.cargoInfo};
    }
}
