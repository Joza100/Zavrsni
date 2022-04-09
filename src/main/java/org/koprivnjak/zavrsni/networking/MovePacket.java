package org.koprivnjak.zavrsni.networking;

public class MovePacket implements Packet {
    private String lan;

    public MovePacket(String lan){
        this.lan = lan;
    }

    public String getLan() {
        return lan;
    }
}
