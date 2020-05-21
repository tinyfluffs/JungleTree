package org.jungletree.core.handler.status;

import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.status.StatusPingPacket;

public class StatusPingHandler implements Handler<StatusPingPacket> {

    @Override
    public void handle(Session session, StatusPingPacket pkt) {
        session.send(pkt);
    }
}
