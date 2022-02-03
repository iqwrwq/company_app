package de.iqwrwq.client;

import de.iqwrwq.core.Kernel;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public abstract class Client extends Thread {

    protected final @NotNull Kernel core;
    protected Socket toServerSocket;
    private final String toServerIp;
    private final int toServerPort;

    public Client(@NotNull Kernel core) {
        this.core = core;
        this.toServerIp = core.config.host;
        this.toServerPort = core.config.port;
    }

    @Override
    public void run() {
        try {
            launch();
        } catch (IOException e) {
            System.out.println("error");
            reconnect();
        } finally {
            CommunicationHandler.forceMessage(this.getClass().getName(), "ConnectionToSeaTradeEnded" + req.DIVIDER + "✔", "\u001B[33m");
        }
    }

    private void launch() throws IOException {
        int maxConnectionAttempts = Integer.parseInt(core.config.getProperty("ConnectionAttempts"));
        for (int connectionAttempt = 0; connectionAttempt <= maxConnectionAttempts; connectionAttempt++) {
            if (toServerSocket == null) {
                this.toServerSocket = connect();
                register();
                process();
            }
        }
    }

    private @NotNull Socket connect() throws IOException {
        return new Socket(toServerIp, toServerPort);
    }

    abstract void reconnect();

    abstract void register() throws IOException;

    abstract void process() throws IOException;

    abstract void handle(String request);

    public abstract void exit();


}
