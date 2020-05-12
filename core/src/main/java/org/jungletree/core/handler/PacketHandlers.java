package org.jungletree.core.handler;

import org.jungletree.net.packet.status.StatusPingPacket;
import org.jungletree.net.protocol.Protocols;

public final class PacketHandlers {
    
    private PacketHandlers() {}
    
    public static void registerAll() {
        Protocols.STATUS.protocol().handler(StatusPingPacket.class, StatusPingHandler.class);
    }
}
