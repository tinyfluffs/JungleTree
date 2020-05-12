package org.jungletree.net.protocol;

import org.jungletree.net.codec.status.StatusPingCodec;
import org.jungletree.net.codec.status.StatusRequestCodec;
import org.jungletree.net.codec.status.StatusResponseCodec;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.packet.status.StatusRequestPacket;
import org.jungletree.net.packet.status.StatusResponsePacket;

public class StatusProtocol extends Protocol {
    
    public StatusProtocol() {
        super("STATUS", 2);

        inbound(0x00, StatusRequestPacket.class, StatusRequestCodec.class);
        inbound(0x01, StatusPingPacket.class, StatusPingCodec.class);

        outbound(0x00, StatusResponsePacket.class, StatusResponseCodec.class);
        outbound(0x01, StatusPingPacket.class, StatusPingCodec.class);
    }
}
