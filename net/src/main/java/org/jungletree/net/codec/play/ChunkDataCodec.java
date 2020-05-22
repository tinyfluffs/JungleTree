package org.jungletree.net.codec.play;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.play.ChunkDataPacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.*;

public class ChunkDataCodec implements Codec<ChunkDataPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataPacket p) throws IOException {
        buf.writeInt(p.getChunkX());
        buf.writeInt(p.getChunkZ());
        buf.writeBoolean(p.isFullChunk());
        writeChunk(buf, p.getChunk(), p.isFullChunk());
        return buf;
    }

    @Override
    public ChunkDataPacket decode(ByteBuf buf) throws IOException {
        return null;
    }
}
