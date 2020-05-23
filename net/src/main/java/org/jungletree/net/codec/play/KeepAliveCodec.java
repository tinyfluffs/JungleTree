package org.jungletree.net.codec.play;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.play.KeepAlivePacket;

public class KeepAliveCodec implements Codec<KeepAlivePacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, KeepAlivePacket p) {
        buf.writeLong(p.getId());
        return buf;
    }

    @Override
    public KeepAlivePacket decode(ByteBuf buf) {
        return KeepAlivePacket.builder()
                .id(buf.readLong())
                .build();
    }
}
