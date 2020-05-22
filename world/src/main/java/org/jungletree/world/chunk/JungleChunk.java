package org.jungletree.world.chunk;

import lombok.Getter;
import org.jungletree.api.world.Chunk;
import org.jungletree.api.world.ChunkSection;

import static org.jungletree.api.world.World.CHUNK_WIDTH;

public class JungleChunk implements Chunk {

    @Getter private final JungleChunkSection[] sections;
    @Getter private final byte[] biomes = new byte[CHUNK_WIDTH * CHUNK_WIDTH];

    public JungleChunk(int height) {
        this.sections = new JungleChunkSection[height / CHUNK_WIDTH + ((height % CHUNK_WIDTH == 0) ? 0 : 1)];
    }

    @Override
    public ChunkSection getSection(int index) {
        if (index >= sections.length) {
            throw new IndexOutOfBoundsException(String.format("Section out of bounds: l=%d, i=%d", sections.length, index));
        }
        if (sections[index] == null) {
            sections[index] = new JungleChunkSection();
        }
        return sections[index];
    }
}
