package de.iqwrwq.core;

import de.iqwrwq.client.Company;
import de.iqwrwq.config.Config;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.ui.CommandUserInterface;

public class Kernel{
    public final Config config;
    public final Company company;
    public final ShipServer shipServer;
    public final CommandUserInterface userInterface;

    public Kernel() {
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
        Kernel core = new Kernel();
        core.boot();
    }

}
