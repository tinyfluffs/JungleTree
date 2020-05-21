package org.jungletree.core.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.core.handler.handshake.HandshakeHandler;
import org.jungletree.core.handler.login.LoginStartHandler;
import org.jungletree.core.handler.status.StatusPingHandler;
import org.jungletree.core.handler.status.StatusRequestHandler;
import org.jungletree.net.packet.handshake.HandshakePacket;
import org.jungletree.net.packet.login.LoginStartPacket;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.packet.status.StatusRequestPacket;
import org.jungletree.net.protocol.Protocols;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PacketHandlers {

    public static void registerAll() {
        Protocols.HANDSHAKE.getProtocol().handler(HandshakePacket.class, HandshakeHandler.class);

        Protocols.STATUS.getProtocol().handler(StatusPingPacket.class, StatusPingHandler.class);
        Protocols.STATUS.getProtocol().handler(StatusRequestPacket.class, StatusRequestHandler.class);

        Protocols.LOGIN.getProtocol().handler(LoginStartPacket.class, LoginStartHandler.class);
    }
}
