package org.jungletree.core.handler.login;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.login.EncryptionResponsePacket;

@Log4j2
public class EncryptionResponseHandler implements Handler<EncryptionResponsePacket> {

    @Override
    public void handle(Session session, EncryptionResponsePacket pkt) {
        log.info(pkt);
    }
}
