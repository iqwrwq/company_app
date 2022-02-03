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
    }

    abstract void handleCommand(String userInput);

    abstract void endApplication();
}
