package org.jungletree.net.protocol;

import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.packet.status.StatusRequestPacket;
import org.jungletree.net.packet.status.StatusResponsePacket;

public class StatusProtocol extends Protocol {

    public StatusProtocol() {
        super("STATUS");

        inbound(0x00, StatusRequestPacket.class);
        inbound(0x01, StatusPingPacket.class);

        outbound(0x00, StatusResponsePacket.class);
        outbound(0x01, StatusPingPacket.class);
    }
}
