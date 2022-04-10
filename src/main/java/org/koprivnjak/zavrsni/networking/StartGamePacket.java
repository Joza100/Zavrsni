package org.koprivnjak.zavrsni.networking;

public class StartGamePacket implements Packet{
    private String side;

    public StartGamePacket(String side){
        this.side = side;
    }

    public String getSide() {
        return side;
    }
}
