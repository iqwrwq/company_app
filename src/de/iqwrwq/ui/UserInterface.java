package de.iqwrwq.ui;

import de.iqwrwq.core.Kernel;

import java.util.Scanner;

public abstract class UserInterface extends Thread {

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        do {
            userInput = scanner.nextLine();
            handleCommand(userInput);
        } while (!userInput.equalsIgnoreCase("exit"));
        endApplication();
    }

    abstract void handleCommand(String userInput);

    abstract void endApplication();
}
