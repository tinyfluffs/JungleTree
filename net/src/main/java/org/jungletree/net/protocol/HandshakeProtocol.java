package org.jungletree.net.protocol;

import org.jungletree.net.codec.handshake.HandshakeCodec;
import org.jungletree.net.packet.handshake.HandshakePacket;

public class HandshakeProtocol extends Protocol {

    public HandshakeProtocol() {
        super("HANDSHAKE", 0);

        inbound(0x00, HandshakePacket.class, HandshakeCodec.class);
    }
}
