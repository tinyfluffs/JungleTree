package org.jungletree.core.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.protocol.Protocols;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PacketHandlers {

    public static void registerAll() {
        Protocols.STATUS.getProtocol().handler(StatusPingPacket.class, StatusPingHandler.class);
    }
}
