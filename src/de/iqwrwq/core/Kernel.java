package de.iqwrwq.core;

import de.iqwrwq.auto.AutoApplicationBot;
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
    public CommandUserInterface userInterface;
    public AutoApplicationBot autoApplicationBot;

    public Kernel() {
        this.config = new Config("config/config.properties");
        this.company = new Company(this);
        this.shipServer = new ShipServer(this);
        Logger.info("Core created");
    }

    public void boot() {
        try {
            if (config.welcomeMessage) welcomeMessage();
            company.start();
            shipServer.start();
            startApplicationMode();
        } catch (Exception ignored) {
        } finally {
            Logger.info("Core booted");
        }
    }

    private void startApplicationMode() {
        if (!config.fullAutoMode){
            this.userInterface = new CommandUserInterface(this);
            userInterface.start();
        }else{
            this.autoApplicationBot = new AutoApplicationBot(this);
            autoApplicationBot.start();
        }
    }

    private void welcomeMessage() {
        System.out.println("\u001B[34m ________  ________  _____ ______   ________  ________  ________       ___    ___  \u001B[0m");
        System.out.println("\u001B[34m|\\   ____\\|\\   __  \\|\\   _ \\  _   \\|\\   __  \\|\\   __  \\|\\   ___  \\    |\\  \\  /  /|  \u001B[0m");
        System.out.println("\u001B[34m\\ \\  \\___|\\ \\  \\|\\  \\ \\  \\\\\\__\\ \\  \\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\\\ \\  \\   \\ \\  \\/  / /  \u001B[0m");
        System.out.println("\u001B[36m \\ \\  \\    \\ \\  \\\\\\  \\ \\  \\\\|__| \\  \\ \\   ____\\ \\   __  \\ \\  \\\\ \\  \\   \\ \\    / /   \u001B[0m");
        System.out.println("\u001B[36m  \\ \\  \\____\\ \\  \\\\\\  \\ \\  \\    \\ \\  \\ \\  \\___|\\ \\  \\ \\  \\ \\  \\\\ \\  \\   \\/  /  /    \u001B[0m");
        System.out.println("\u001B[36m   \\ \\_______\\ \\_______\\ \\__\\    \\ \\__\\ \\__\\    \\ \\__\\ \\__\\ \\__\\\\ \\__\\__/  / /      \u001B[0m");
        System.out.println("\u001B[34m    \\|_______|\\|_______|\\|__|     \\|__|\\|__|     \\|__|\\|__|\\|__| \\|__|\\___/ /       \u001B[0m");
        System.out.println("\u001B[36m ________  ________  ________        ___      ___  _____      ________|________     \u001B[0m");
        System.out.println("\u001B[36m|\\   __  \\|\\   __  \\|\\   __  \\      |\\  \\    /  /|/ __  \\    |\\   __  \\  / __  \\    \u001B[0m");
        System.out.println("\u001B[36m \\ \\   __  \\ \\   ____\\ \\   ____\\     \\ \\  \\/  / /\\|/ \\ \\  \\   \\ \\  \\\\\\  \\|/ \\ \\  \\  \u001B[0m");
        System.out.println("\u001B[34m  \\ \\  \\ \\  \\ \\  \\___|\\ \\  \\___|      \\ \\    / /__    \\ \\  \\ __\\ \\  \\\\\\  \\   \\ \\  \\ \u001B[0m");
        System.out.println("\u001B[34m   \\ \\__\\ \\__\\ \\__\\    \\ \\__\\          \\ \\__/ /\\__\\    \\ \\__\\\\__\\ \\_______\\   \\ \\__\\\u001B[0m");
        System.out.println("\u001B[36m    \\|__|\\|__|\\|__|     \\|__|           \\|__|/\\|__|     \\|__\\|__|\\|_______|    \\|__|\u001B[0m");
        System.out.println("                                                                                    ");
    }

    public static void main(String[] args) {
        Kernel core = new Kernel();
        core.boot();
    }

}
