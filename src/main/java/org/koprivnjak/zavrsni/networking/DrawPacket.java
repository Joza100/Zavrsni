package org.koprivnjak.zavrsni.networking;

public class DrawPacket implements Packet {
    public enum DrawRequest {
        OFFER,
        ACCEPT,
        DECLINE
    }
    private DrawRequest drawRequest;

    public DrawPacket(DrawRequest drawRequest){
        this.drawRequest = drawRequest;
    }

    public DrawRequest getDrawRequest() {
        return drawRequest;
    }
}
