package de.iqwrwq.ui;

import java.util.Scanner;

public abstract class UserInterface extends Thread {

    public static String INSTANCE_NAME = "UserInterface";

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        CommunicationHandler.forceMessage(INSTANCE_NAME, "isListening", "\u001B[32m");

        do {
            userInput = scanner.nextLine();
            handleCommand(userInput.toLowerCase());
        } while (!userInput.equalsIgnoreCase("exit"));
        endApplication();
        try {
            sleep(500);
            goodByeMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    abstract void handleCommand(String userInput);

    abstract void endApplication();

    public void goodByeMessage() {
        System.out.println("\u001B[34m ________  ________  ________        _______   ________   ________  _______   ________     \u001B[0m");
        System.out.println("\u001B[34m|\\   __  \\|\\   __  \\|\\   __  \\      |\\  ___ \\ |\\   ___  \\|\\   ___ \\|\\  ___ \\ |\\   ___ \\    \u001B[0m");
        System.out.println("\u001B[34m\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\|\\  \\     \\ \\   __/|\\ \\  \\\\ \\  \\ \\  \\_|\\ \\ \\   __/|\\ \\  \\_|\\ \\   \u001B[0m");
        System.out.println("\u001B[34m \\ \\   __  \\ \\   ____\\ \\   ____\\     \\ \\  \\_|/_\\ \\  \\\\ \\  \\ \\  \\ \\\\ \\ \\  \\_|/_\\ \\  \\ \\\\ \\  \u001B[0m");
        System.out.println("\u001B[36m  \\ \\  \\ \\  \\ \\  \\___|\\ \\  \\___|      \\ \\  \\_|\\ \\ \\  \\\\ \\  \\ \\  \\_\\\\ \\ \\  \\_|\\ \\ \\  \\_\\\\ \\ \u001B[0m");
        System.out.println("\u001B[36m   \\ \\__\\ \\__\\ \\__\\    \\ \\__\\          \\ \\_______\\ \\__\\\\ \\__\\ \\_______\\ \\_______\\ \\_______\\\u001B[0m");
        System.out.println("\u001B[36m    \\|__|\\|__|\\|__|     \\|__|           \\|_______|\\|__| \\|__|\\|_______|\\|_______|\\|_______|\u001B[0m");
        System.out.println("                                                                                           ");

    }
}
