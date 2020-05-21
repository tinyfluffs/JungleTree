package org.jungletree.core.handler.handshake;

import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.handshake.HandshakePacket;

public class HandshakeHandler implements Handler<HandshakePacket> {

    @Override
    public void handle(Session session, HandshakePacket pkt) {
        session.setProtocol(pkt.getNextState().getProtocol());
    }
}
