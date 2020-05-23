package org.jungletree.net.codec.play;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.exception.PluginDataException;
import org.jungletree.net.packet.play.PluginDataPacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.readString;
import static org.jungletree.net.ByteBufUtils.writeString;

public class PluginDataCodec implements Codec<PluginDataPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, PluginDataPacket p) throws IOException {
        writeString(buf, p.getChannel());
        var data = p.getData();
        if (data.length > Short.MAX_VALUE) {
            throw new PluginDataException(String.format(
                    "Plugin data exceeds the maximum length of %d for channel \"%s\": actualLength=%d",
                    Short.MAX_VALUE, p.getChannel(), data.length
            ));
        }
        buf.writeBytes(data);
        return buf;
    }

    @Override
    public PluginDataPacket decode(ByteBuf buf) throws IOException {
        var channel = readString(buf);
        var length = buf.readableBytes();
        if (length > Short.MAX_VALUE) {
            throw new PluginDataException(String.format(
                    "Plugin data exceeds the maximum length of %d for channel \"%s\": actualLength=%d",
                    Short.MAX_VALUE, channel, length
            ));
        }
        byte[] data = new byte[length];
        buf.readBytes(data);
        return PluginDataPacket.builder()
                .channel(channel)
                .data(data)
                .build();
    }
}
