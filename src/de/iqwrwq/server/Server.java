package de.iqwrwq.server;

import de.iqwrwq.config.Config;
import de.iqwrwq.ui.CommunicationHandler;
import de.iqwrwq.ui.req;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class Server extends Thread{

    protected ServerSocket serverSocket;
    protected final int port;

    public Server(@NotNull Config config){
        this.port = config.serverPort;
    }

    @Override
    public void run() {
        try {
            connect();
            process();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommunicationHandler.forceMessage(this.getClass().getName(), "ServerShutDown" + req.DIVIDER + "✔", "\u001B[33m");
        }
    }

    abstract void connect() throws IOException;

    abstract void process() throws IOException;

    abstract void exit();
}
