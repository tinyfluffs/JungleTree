package org.jungletree.net.codec.status;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.status.StatusResponsePacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.readString;
import static org.jungletree.net.ByteBufUtils.writeString;

public class StatusResponseCodec implements Codec<StatusResponsePacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, StatusResponsePacket p) throws IOException {
        writeString(buf, p.getJson());
        return buf;
    }

    @Override
    public StatusResponsePacket decode(ByteBuf buf) throws IOException {
        return StatusResponsePacket.builder()
                .json(readString(buf))
                .build();
    }
}
