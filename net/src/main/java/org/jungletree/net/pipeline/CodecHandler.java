package org.jungletree.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.protocol.Protocol;

import java.util.List;

import static org.jungletree.net.ByteBufUtils.readVarInt;
import static org.jungletree.net.ByteBufUtils.writeVarInt;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class CodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    Protocol protocol;

    public CodecHandler(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
        Class<? extends Packet> clazz = msg.getClass();
        Codec.CodecRegistration reg = protocol.getCodecRegistration(clazz);
        if (reg == null) {
            throw new EncoderException("Unknown message type: " + clazz + ".");
        }

        ByteBuf headerBuf = ctx.alloc().buffer();
        writeVarInt(headerBuf, reg.getOpcode());
        ByteBuf messageBuf = ctx.alloc().buffer();
        messageBuf = reg.getCodec().encode(messageBuf, msg);
        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int id = readVarInt(msg);
        Codec<?> codec = protocol.find(id);
        Packet decoded = codec.decode(msg);
        if (msg.readableBytes() > 0) {
            log.warn("Packet was not fully read: remaining={}, packet={}", msg.readableBytes(), decoded);
        }
        out.add(decoded);
    }
}
