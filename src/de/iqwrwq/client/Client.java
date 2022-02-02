package de.iqwrwq.client;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;

import java.io.IOException;
import java.net.Socket;

public abstract class Client extends Thread {

    protected final Kernel core;
    protected Socket toServerSocket;
    private final String toServerIp;
    private final int toServerPort;

    public Client(Kernel core) {
        this.core = core;
        this.toServerIp = core.config.host;
        this.toServerPort = core.config.port;
    }

    @Override
    public void run() {
        try {
            launch();
        } catch (IOException e) {
            reconnect();
        } finally {
            CommunicationHandler.forceMessage(this.getClass().getName(), "ConnectionToSeaTradeEnded" + req.DIVIDER + "✔", "\u001B[33m");
        }
    }

    private void launch() throws IOException {
        int maxConnectionAttempts = Integer.parseInt(core.config.getProperty("ConnectionAttempts"));
        for (int connectionAttempt = 0; connectionAttempt <= maxConnectionAttempts; connectionAttempt++) {
            this.toServerSocket = connect();
            register();
            process();
        }
    }

    private Socket connect() throws IOException {
        return new Socket(toServerIp, toServerPort);
    }

    abstract void reconnect();

    abstract void register() throws IOException;

    abstract void process() throws IOException;

    abstract void handle(String request);

    public abstract void exit();


}
