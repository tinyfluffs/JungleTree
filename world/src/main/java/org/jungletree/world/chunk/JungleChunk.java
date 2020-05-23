package org.jungletree.world.chunk;

import lombok.Getter;
import lombok.ToString;
import org.jungletree.api.world.Chunk;
import org.jungletree.api.world.ChunkSection;

import static org.jungletree.api.world.World.CHUNK_SECTION_HEIGHT;
import static org.jungletree.api.world.World.CHUNK_SECTION_WIDTH;

@ToString
public class JungleChunk implements Chunk {

    @Getter private final byte[] biomes;
    @Getter private final byte[] heightMap;
    @Getter private final JungleChunkSection[] sections;

    public JungleChunk(int height) {
        this.biomes = new byte[CHUNK_SECTION_WIDTH * CHUNK_SECTION_HEIGHT];
        this.heightMap = new byte[CHUNK_SECTION_WIDTH * CHUNK_SECTION_HEIGHT];
        this.sections = new JungleChunkSection[height / CHUNK_SECTION_HEIGHT + ((height % CHUNK_SECTION_HEIGHT == 0) ? 0 : 1)];

        for (int i=0; i<sections.length; i++) {
            this.sections[i] = new JungleChunkSection();
        }
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
