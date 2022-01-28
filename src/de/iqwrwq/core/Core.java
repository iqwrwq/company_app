package de.iqwrwq.core;

import de.iqwrwq.config.Config;

public class Core{

    public static boolean keepChatWatching = true;
    public final Config config;
    public final Company company;
    public final ShipServer shipServer;
    public final CommandUserInterface userInterface;

    public Core() {
        this.config = new Config("config/config.properties");
        this.company = new Company(this);
        this.shipServer = new ShipServer(this);
        this.userInterface = new CommandUserInterface(this);
    }

    public void boot(){
        company.start();
        shipServer.start();
        userInterface.start();
    }

    public static void main(String[] args) {
        Core core = new Core();
        core.boot();
    }
}
