package org.koprivnjak.zavrsni.networking;

import javafx.concurrent.Task;

import java.net.ServerSocket;
import java.net.Socket;

public class WaitForPlayerTask extends Task<Socket> {
    private ServerSocket serverSocket;

    public WaitForPlayerTask(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    protected Socket call() throws Exception {
        return serverSocket.accept();
    }
}
