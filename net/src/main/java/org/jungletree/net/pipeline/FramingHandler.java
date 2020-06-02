package org.jungletree.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.jungletree.net.FriendlyByteBuf;

import java.util.List;

public final class FramingHandler extends ByteToMessageCodec<ByteBuf> {

    private static boolean readableVarInt(ByteBuf buf) {
        if (buf.readableBytes() > 5) {
            // maximum varint size
            return true;
        }

        int idx = buf.readerIndex();
        byte in;
        do {
            if (buf.readableBytes() < 1) {
                buf.readerIndex(idx);
                return false;
            }
            in = buf.readByte();
        } while ((in & 0x80) != 0);

        buf.readerIndex(idx);
        return true;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        FriendlyByteBuf buf = new FriendlyByteBuf(out);
        buf.writeVarInt(msg.readableBytes());
        buf.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        // check for length field readability
        msg.markReaderIndex();
        if (!readableVarInt(msg)) {
            return;
        }

        FriendlyByteBuf in = new FriendlyByteBuf(msg);

        // check for contents readability
        int length = in.readVarInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // read contents into buf
        ByteBuf buf = ctx.alloc().buffer(length);
        in.readBytes(buf, length);
        out.add(buf);
    }
}
