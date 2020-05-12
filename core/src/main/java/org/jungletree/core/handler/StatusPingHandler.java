package org.jungletree.core.handler;

import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.session.Session;

public class StatusPingHandler implements Handler<StatusPingPacket> {

    @Override
    public void handle(Session session, StatusPingPacket pkt) {
    }
}
