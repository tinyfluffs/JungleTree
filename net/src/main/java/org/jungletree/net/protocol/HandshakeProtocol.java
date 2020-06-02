package org.jungletree.net.protocol;

import org.jungletree.net.packet.handshake.HandshakePacket;

public class HandshakeProtocol extends Protocol {

    public HandshakeProtocol() {
        super("HANDSHAKE");

        inbound(0x00, HandshakePacket.class);
    }
}
