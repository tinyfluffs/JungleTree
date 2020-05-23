package org.jungletree.core.handler.play;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.play.KeepAlivePacket;

@Log4j2
public class KeepAliveHandler implements Handler<KeepAlivePacket> {

    @Override
    public void handle(Session session, KeepAlivePacket pkt) {
        log.info("Inbound: {}", pkt);
    }
}
