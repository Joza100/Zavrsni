package org.koprivnjak.zavrsni.networking;

import javafx.concurrent.Task;
import java.net.Socket;

public class ConnectTask extends Task<Socket> {

    private String ipAddress;
    private int port;

    public ConnectTask(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    protected Socket call() throws Exception {
        return new Socket(ipAddress, port);
    }
}
