package de.iqwrwq.core;

import de.iqwrwq.client.Company;
import de.iqwrwq.config.Config;
import de.iqwrwq.server.ShipServer;
import de.iqwrwq.ui.CommandUserInterface;
import de.iqwrwq.ui.CommunicationHandler;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;


public class Kernel {
    public static final String INSTANCE_NAME = "Kernel";
    public final @NotNull Config config;
    public final @NotNull Company company;
    public final @NotNull ShipServer shipServer;
    public final @NotNull CommandUserInterface userInterface;

    public Kernel() {
        this.config = new Config("config/config.properties");
        this.company = new Company(this);
        this.shipServer = new ShipServer(this);
        this.userInterface = new CommandUserInterface(this);
        Logger.info("Core created");
    }

    public void boot() {
        try {
            company.start();
            shipServer.start();
            userInterface.start();
        } catch (Exception ignored) {
        } finally {
            Logger.info("Core booted");
        }
    }

    public static void main(String[] args) {
        Kernel core = new Kernel();
        core.boot();
    }

}
