package org.jungletree.net.protocol;

import org.jungletree.net.codec.DisconnectCodec;
import org.jungletree.net.codec.play.ChunkDataCodec;
import org.jungletree.net.codec.play.KeepAliveCodec;
import org.jungletree.net.packet.DisconnectPacket;
import org.jungletree.net.packet.play.ChunkDataPacket;
import org.jungletree.net.packet.play.KeepAlivePacket;

public class PlayProtocol extends Protocol {

    public PlayProtocol() {
        super("PLAY", 0x4F);

        inbound(0x0F, KeepAlivePacket.class, KeepAliveCodec.class);

        outbound(0x1B, DisconnectPacket.class, DisconnectCodec.class);
        outbound(0x21, KeepAlivePacket.class, KeepAliveCodec.class);
        outbound(0x22, ChunkDataPacket.class, ChunkDataCodec.class);
    }
}
