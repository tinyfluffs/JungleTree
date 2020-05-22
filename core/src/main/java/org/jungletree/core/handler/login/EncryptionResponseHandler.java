package org.jungletree.core.handler.login;

import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.login.EncryptionResponsePacket;

public class EncryptionResponseHandler implements Handler<EncryptionResponsePacket> {

    @Override
    public void handle(Session session, EncryptionResponsePacket pkt) {
        session.enableEncryption(session, pkt.getSharedSecret(), pkt.getVerifyToken());
    }
}
