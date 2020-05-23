package org.jungletree.world.chunk;

import lombok.Getter;
import org.jungletree.api.world.ChunkSection;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jungletree.api.world.World.*;

public class JungleChunkSection implements ChunkSection {

    @Getter private final long[] blocks;
    private final AtomicInteger lazyBlockCount;

    public JungleChunkSection() {
        this.blocks = new long[CHUNK_SECTION_SIZE];
        this.lazyBlockCount = new AtomicInteger(0);
        recalculateEmpty();
    }

    @Override
    public long getBlockAt(int x, int y, int z) {
        return blocks[(y << 8) | (z << 4) | x];
    }

    @Override
    public void setBlockAt(int x, int y, int z, long state) {
        blocks[(y << 8) | (z << 4) | x] = state;
    }

    @Override
    public boolean isEmpty() {
        return this.lazyBlockCount.get() == 0;
    }

    @Override
    public void fill(long state) {
        Arrays.fill(this.blocks, state);
    }

    public short getLazyBlockCount() {
        return (short) lazyBlockCount.get();
    }

    @Override
    public void recalculateEmpty() {
        int count = 0;
        for (int x = 0; x < CHUNK_SECTION_WIDTH; x++) {
            for (int y = 0; y < CHUNK_SECTION_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_SECTION_DEPTH; z++) {
                    if (getBlockAt(x, y, z) != 0) {
                        count++;
                    }
                }
            }
        }
        this.lazyBlockCount.set(count);
    }
}
