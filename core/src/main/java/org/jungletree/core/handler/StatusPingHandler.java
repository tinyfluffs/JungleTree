package org.jungletree.core.handler;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.session.Session;

@Log4j2
public class StatusPingHandler implements Handler<StatusPingPacket> {

    @Override
    public void handle(Session session, StatusPingPacket pkt) {
        log.info(pkt);
    }
}
