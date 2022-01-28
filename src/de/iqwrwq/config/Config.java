package de.iqwrwq.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Config extends Properties {

    private static final String SEPARATOR = " -> ";

    public String host;
    public int port;
    public int shipServerPort;
    public String companyName;
    public int maxShips;

    public Config(String path) {
        try {
            this.load(new FileReader(path));
            this.host = getProperty("host");
            this.port = Integer.parseInt(getProperty("port"));
            this.shipServerPort = Integer.parseInt(getProperty("shipServerPort"));
            this.companyName = getProperty("companyName");
            this.maxShips =  getProperty("maxShipsAllowed").isEmpty() ? 999 : Integer.parseInt(getProperty("maxShipsAllowed"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

