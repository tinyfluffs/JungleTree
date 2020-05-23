package org.jungletree.core.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.core.handler.handshake.HandshakeHandler;
import org.jungletree.core.handler.login.EncryptionResponseHandler;
import org.jungletree.core.handler.login.LoginStartHandler;
import org.jungletree.core.handler.play.KeepAliveHandler;
import org.jungletree.core.handler.status.StatusPingHandler;
import org.jungletree.core.handler.status.StatusRequestHandler;
import org.jungletree.net.packet.handshake.HandshakePacket;
import org.jungletree.net.packet.login.EncryptionResponsePacket;
import org.jungletree.net.packet.login.LoginStartPacket;
import org.jungletree.net.packet.play.KeepAlivePacket;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.packet.status.StatusRequestPacket;

import static org.jungletree.net.protocol.Protocols.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PacketHandlers {

    public static void registerAll() {
        var handshake = HANDSHAKE.getProtocol();
        handshake.handler(HandshakePacket.class, HandshakeHandler.class);

        var status = STATUS.getProtocol();
        status.handler(StatusPingPacket.class, StatusPingHandler.class);
        status.handler(StatusRequestPacket.class, StatusRequestHandler.class);

        var login = LOGIN.getProtocol();
        login.handler(LoginStartPacket.class, LoginStartHandler.class);
        login.handler(EncryptionResponsePacket.class, EncryptionResponseHandler.class);

        var play = PLAY.getProtocol();
        play.handler(KeepAlivePacket.class, KeepAliveHandler.class);
    }
}
