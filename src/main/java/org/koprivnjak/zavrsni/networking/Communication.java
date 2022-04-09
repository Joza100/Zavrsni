package org.koprivnjak.zavrsni.networking;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Communication implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running;

    private PacketReceivedListener packetReceivedListener;

    public Communication(Socket socket){
        this.socket = socket;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(this);
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet){
        try {
            out.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            try {
                Packet packet = (Packet) in.readObject();
                if(packetReceivedListener != null){
                    packetReceivedListener.onPacketReceived(packet);
                }
            } catch (SocketException | EOFException e){
                running = false;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){
        try {
            running = false;
            if (socket != null){
                out.close();
                in.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPacketReceivedListener(PacketReceivedListener packetReceivedListener) {
        this.packetReceivedListener = packetReceivedListener;
    }
}
