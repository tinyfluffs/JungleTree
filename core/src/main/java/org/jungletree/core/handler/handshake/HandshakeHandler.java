package org.jungletree.core.handler.handshake;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.handshake.HandshakePacket;

@Log4j2
public class HandshakeHandler implements Handler<HandshakePacket> {

    @Override
    public void handle(Session session, HandshakePacket pkt) {
        session.setProtocol(pkt.getNextState().getProtocol());
        log.info(pkt);
    }
}
