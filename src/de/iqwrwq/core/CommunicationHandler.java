package de.iqwrwq.core;

import java.io.PrintWriter;

public class CommunicationHandler {

    private final String origin;
    private final PrintWriter printWriter;

    public CommunicationHandler(String origin, PrintWriter printWriter) {
        this.origin = origin;
        this.printWriter = printWriter;
    }

    public void notifyApp(String message) {
        System.out.println(origin + req.SEPARATOR + message);
    }

    public void notifyApp(String message, String modifier) {
        System.out.println(modifier + origin + req.SEPARATOR + message + req.RESET);
    }

    public void notifyServer(String message) {
        printWriter.println(message);
    }

    public void notifyAll(String message) {
        System.out.println(origin + req.SEPARATOR + message);
        printWriter.println(message);
    }

    public void plainServerMessage(String message) {
        printWriter.println(message);
    }

    public static void forceMessage(String origin, String message) {
        System.out.println(origin + req.SEPARATOR + message);
    }

    public static void forceMessage(String origin, String message, String modifier) {
        System.out.println(modifier + origin + req.SEPARATOR + message + req.RESET);
    }


}

class req {
    public static final String DIVIDER = " | ";
    public static final String SEPARATOR = " -> ";
    public static final String RESET = "\u001B[0m";
}

