package org.jungletree.net.packet.status;

import org.jungletree.net.Packet;

public class StatusResponsePacket implements Packet {

    private final String json;
    
    public StatusResponsePacket(String json) {
        this.json = json;
    }
}
