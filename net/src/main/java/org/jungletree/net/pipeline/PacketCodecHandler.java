package org.jungletree.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;
import org.jungletree.net.protocol.Protocol;

import java.util.List;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    Protocol protocol;

    public PacketCodecHandler(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) {
        FriendlyByteBuf headerBuf = new FriendlyByteBuf(ctx.alloc().buffer());
        headerBuf.writeVarInt(protocol.getPacketId(msg.getClass()));
        FriendlyByteBuf messageBuf = new FriendlyByteBuf(ctx.alloc().buffer());
        msg.encode(messageBuf);
        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        FriendlyByteBuf buf = new FriendlyByteBuf(msg);
        int id = buf.readVarInt();
        Packet packet = protocol.find(id);
        packet.decode(buf);
        if (msg.readableBytes() > 0) {
            log.warn("Packet was not fully read: remaining={}, packet={}", msg.readableBytes(), packet);
        }
        out.add(packet);
    }
}
