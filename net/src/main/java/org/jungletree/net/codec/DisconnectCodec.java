package org.jungletree.net.codec;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.DisconnectPacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.readChatMessage;
import static org.jungletree.net.ByteBufUtils.writeChatMessage;

public class DisconnectCodec implements Codec<DisconnectPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, DisconnectPacket p) throws IOException {
        writeChatMessage(buf, p.getReason());
        return buf;
    }

    @Override
    public DisconnectPacket decode(ByteBuf buf) throws IOException {
        return DisconnectPacket.builder()
                .reason(readChatMessage(buf))
                .build();
    }
}
