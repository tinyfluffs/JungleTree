package org.jungletree.net.packet.status;

import org.jungletree.net.Packet;

import java.util.StringJoiner;

public class StatusRequestPacket implements Packet {

    @Override
    public String toString() {
        return new StringJoiner(", ", StatusRequestPacket.class.getSimpleName() + "[", "]")
                .toString();
    }
}
