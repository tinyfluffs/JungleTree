package org.jungletree.net.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.play.ChunkDataPacket;
import org.jungletree.world.chunk.JungleChunk;

import static org.jungletree.net.ByteBufUtils.writeChunk;

public class ChunkDataCodec implements Codec<ChunkDataPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataPacket p) {
        ByteBuf b = Unpooled.buffer();
        b.writeInt(p.getChunkX());
        b.writeInt(p.getChunkZ());
        b.writeBoolean(p.isFullChunk());
        writeChunk(b, (JungleChunk) p.getChunk(), p.isFullChunk(), 0);
        return buf;
    }

    @Override
    public ChunkDataPacket decode(ByteBuf buf) {
        return null;
    }
}
