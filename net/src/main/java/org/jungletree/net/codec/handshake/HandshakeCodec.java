package org.jungletree.net.codec.handshake;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.handshake.HandshakePacket;
import org.jungletree.net.protocol.Protocols;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.*;

public class HandshakeCodec implements Codec<HandshakePacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakePacket p) throws IOException {
        writeVarInt(buf, p.getProtocolVersion());
        writeString(buf, p.getAddress());
        buf.writeShort(p.getPort());
        writeVarInt(buf, p.getNextState().getId());
        return buf;
    }

    @Override
    public HandshakePacket decode(ByteBuf buf) throws IOException {
        return HandshakePacket.builder()
                .protocolVersion(readVarInt(buf))
                .address(readString(buf))
                .port(buf.readShort())
                .nextState(Protocols.fromId(readVarInt(buf)))
                .build();
    }
}
