package org.jungletree.net.protocol;

import org.jungletree.net.packet.DisconnectPacket;
import org.jungletree.net.packet.play.ChunkDataPacket;
import org.jungletree.net.packet.play.KeepAlivePacket;
import org.jungletree.net.packet.play.PluginDataPacket;

public class PlayProtocol extends Protocol {

    public PlayProtocol() {
        super("PLAY");

        inbound(0x0B, PluginDataPacket.class);
        inbound(0x0F, KeepAlivePacket.class);

        outbound(0x1B, DisconnectPacket.class);
        outbound(0x19, PluginDataPacket.class);
        outbound(0x21, KeepAlivePacket.class);
        outbound(0x22, ChunkDataPacket.class);
    }
}
