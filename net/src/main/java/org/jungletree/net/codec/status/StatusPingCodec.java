package org.jungletree.net.codec.status;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.status.StatusPingPacket;

public class StatusPingCodec implements Codec<StatusPingPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, StatusPingPacket p) {
        buf.writeLong(p.getTime());
        return buf;
    }

    @Override
    public StatusPingPacket decode(ByteBuf buf) {
        return StatusPingPacket.builder()
                .time(buf.readLong())
                .build();
    }
}
