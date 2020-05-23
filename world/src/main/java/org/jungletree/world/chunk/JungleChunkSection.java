package org.jungletree.world.chunk;

import lombok.Getter;
import org.jungletree.api.world.ChunkSection;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jungletree.api.world.World.CHUNK_SECTION_SIZE;
import static org.jungletree.api.world.World.CHUNK_WIDTH;

public class JungleChunkSection implements ChunkSection {

    @Getter private final char[] blocks;
    private final AtomicInteger lazyBlockCount;

    public JungleChunkSection() {
        this.blocks = new char[CHUNK_SECTION_SIZE];
        this.lazyBlockCount = new AtomicInteger(0);
    }

    @Override
    public char getPaletteIdAt(int x, int y, int z) {
        return blocks[(y << 8) | (z << 4) | x];
    }

    @Override
    public void setPaletteIdAt(int x, int y, int z, char paletteId) {
        blocks[(y << 8) | (z << 4) | x] = paletteId;
    }

    @Override
    public boolean isEmpty() {
        return this.lazyBlockCount.get() == 0;
    }

    @Override
    public void fill(char paletteId) {
        Arrays.fill(this.blocks, paletteId);
    }

    public short getLazyBlockCount() {
        return (short) lazyBlockCount.get();
    }

    @Override
    public void recalculateEmpty() {
        int count = 0;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_WIDTH; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    if (getPaletteIdAt(x, y, z) != 0) {
                        count++;
                    }
                }
            }
        }
        this.lazyBlockCount.set(count);
    }
}
