package org.jungletree.core.handler.login;

import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.login.EncryptionRequestPacket;
import org.jungletree.net.packet.login.LoginStartPacket;

import static org.jungletree.api.JungleTree.server;

@Log4j2
public class LoginStartHandler implements Handler<LoginStartPacket> {

    @Override
    public void handle(Session session, LoginStartPacket pkt) {
        log.info(pkt);

        if (server().isEncryptionEnabled()) {
            session.setVerifyUsername(pkt.getUsername());

            session.send(
                    EncryptionRequestPacket.builder()
                            .sessionId(session.getSessionId())
                            .publicKey(server().getPublicKey().getEncoded())
                            .verifyToken(session.generateVerifyToken())
                            .build()
            );
        }
    }
}
