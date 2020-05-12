package org.jungletree.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jungletree.net.ByteBufUtils;
import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.protocol.Protocol;

import java.util.List;

public final class CodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    private static final Logger log = LogManager.getLogger(CodecHandler.class);

    private final Protocol protocol;

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

        ByteBuf headerBuf = ctx.alloc().buffer(Byte.SIZE);
        ByteBufUtils.writeVarInt(headerBuf, reg.getOpcode());
        ByteBuf messageBuf = ctx.alloc().buffer();
        messageBuf = reg.getCodec().encode(messageBuf, msg);
        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Codec<?> codec = protocol.readHeader(msg);
        Packet decoded = codec.decode(msg);
        if (msg.readableBytes() > 0) {
            log.warn("Packet was not fully read: remaining={}, packet={}", msg.readableBytes(), decoded);
        }
        out.add(decoded);
    }
}
