package org.jungletree.net.protocol;

import org.jungletree.net.codec.DisconnectCodec;
import org.jungletree.net.packet.DisconnectPacket;

public class PlayProtocol extends Protocol {

    public PlayProtocol() {
        super("PLAY", 0x4F);

        outbound(0x1B, DisconnectPacket.class, DisconnectCodec.class);
    }
}
