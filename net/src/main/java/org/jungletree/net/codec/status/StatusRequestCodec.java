package org.jungletree.net.codec.status;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.status.StatusRequestPacket;

public class StatusRequestCodec implements Codec<StatusRequestPacket> {
    
    @Override
    public ByteBuf encode(ByteBuf buf, StatusRequestPacket p) {
        return buf;
    }

    @Override
    public StatusRequestPacket decode(ByteBuf buf) {
        return new StatusRequestPacket();
    }
}
