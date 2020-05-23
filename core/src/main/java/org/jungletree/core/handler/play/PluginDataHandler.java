package org.jungletree.core.handler.play;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.play.PluginDataPacket;

@Log4j2
public class PluginDataHandler implements Handler<PluginDataPacket> {

    @Override
    public void handle(Session session, PluginDataPacket pkt) {
        log.info(pkt);
    }
}
