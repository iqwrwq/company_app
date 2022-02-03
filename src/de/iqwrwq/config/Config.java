package de.iqwrwq.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties {

    public String host;
    public int port;
    public int serverPort;
    public String companyName;
    public int maxShips;
    public String[] harbours;

    public Config(String path) {
        try {
            this.load(new FileReader(path));
            this.host = getProperty("host");
            this.port = Integer.parseInt(getProperty("port"));
            this.serverPort = Integer.parseInt(getProperty("shipServerPort"));
            this.companyName = getProperty("companyName");
            this.maxShips =  getProperty("maxShipsAllowed").isEmpty() ? 999 : Integer.parseInt(getProperty("maxShipsAllowed"));
            this.harbours = getProperty("harbours").split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

