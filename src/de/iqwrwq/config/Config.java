package de.iqwrwq.config;

import org.jetbrains.annotations.NotNull;

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
    public boolean initialAutoSync;
    public boolean muteSync;
    public boolean massMove;

    public Config(@NotNull String path) {
        try {
            this.load(new FileReader(path));
            this.host = getProperty("host");
            this.port = Integer.parseInt(getProperty("port"));
            this.serverPort = Integer.parseInt(getProperty("shipServerPort"));
            this.companyName = getProperty("companyName");
            this.maxShips =  getProperty("maxShipsAllowed").isEmpty() ? 999 : Integer.parseInt(getProperty("maxShipsAllowed"));
            this.harbours = getProperty("harbours").split(",");
            this.initialAutoSync = Boolean.parseBoolean(getProperty("initialAutoSync"));
            this.muteSync = Boolean.parseBoolean(getProperty("muteSync"));
            this.massMove = Boolean.parseBoolean(getProperty("massMove"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

