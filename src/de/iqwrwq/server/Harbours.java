package de.iqwrwq.server;

import de.iqwrwq.config.Config;

import java.util.ArrayList;
import java.util.Arrays;

public class Harbours extends ArrayList<Harbour>{

    public Harbours(Config config) {
        super(Arrays.asList(Arrays.stream(config.harbours).map(Harbour::new).toArray(Harbour[]::new)));
    }
}

class Harbour{
    public String name;
    public ArrayList<Ship> ships;

    public Harbour(String name){
        this.name = name;
        this.ships = new ArrayList<Ship>();
    }
}


