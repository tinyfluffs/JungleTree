package org.jungletree.net.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jungletree.api.nbt.Tag;
import org.jungletree.api.world.Palette;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.play.ChunkDataPacket;
import org.jungletree.world.chunk.JungleChunk;
import org.jungletree.world.chunk.JungleChunkSection;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.writeTag;
import static org.jungletree.net.ByteBufUtils.writeVarInt;

public class ChunkDataCodec implements Codec<ChunkDataPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkDataPacket p) throws IOException {
        JungleChunk chunk = (JungleChunk) p.getChunk();
        JungleChunkSection[] sections = chunk.getSections();
        boolean fullChunk = p.isFullChunk();

        ByteBuf b = Unpooled.buffer();
        b.writeInt(p.getChunkX());
        b.writeInt(p.getChunkZ());
        b.writeBoolean(fullChunk);

        int primaryBitMask = getPrimaryBitMask(buf, fullChunk, sections);
        writeVarInt(buf, primaryBitMask);

        writeTag(buf, chunk.getHeightMaps());

        if (fullChunk) {
            buf.writeBytes(chunk.getBiomes());
        }

        ByteBuf chunkBuf = Unpooled.buffer(0);
        try {
            for (int i = 0; i < sections.length; ++i) {
                if ((primaryBitMask & 1 << i) == 0) {
                    continue;
                }
                writeChunkSection(chunkBuf, sections[i]);
            }
            writeVarInt(buf, chunkBuf.writerIndex());
            buf.writeBytes(chunkBuf);
        } finally {
            chunkBuf.release();
        }

        Tag[] tileEntities = chunk.getTileEntities();
        writeVarInt(buf, tileEntities.length);
        for (Tag tileEntity : tileEntities) {
            writeTag(buf, tileEntity);
        }
        return buf;
    }

    private int getPrimaryBitMask(ByteBuf buf, boolean fullChunk, JungleChunkSection[] sections) {
        int primaryBitMask = 0;
        if (fullChunk) {
            primaryBitMask = (1 << sections.length) - 1;
        }
        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] == null || sections[i].isEmpty()) {
                primaryBitMask &= ~(1 << i);
            }
        }
        return primaryBitMask;
    }

    private static void writeChunkSection(ByteBuf buf, JungleChunkSection section) {
        buf.writeShort(section.getLazyBlockCount());

        int bitsPerBlock = Palette.GLOBAL_PALETTE_BITS_PER_BLOCK; // TODO: Chunk palette
        buf.writeByte(bitsPerBlock);

        if (bitsPerBlock > Palette.GLOBAL_PALETTE_BITS_PER_BLOCK) {
            // writePalette(buf);
        }

        long[] blocks = section.getBlocks();
        writeVarInt(buf, blocks.length);
        for (long b : blocks) {
            buf.writeLong(b);
        }
    }

    @Override
    public ChunkDataPacket decode(ByteBuf buf) {
        return null;
    }
}
