package de.iqwrwq.server;

import java.util.ArrayList;

public class Harbour {
    public String name;
    public ArrayList<Ship> ships;

    public Harbour(String name) {
        this.name = name;
        this.ships = new ArrayList<Ship>();
    }
}
