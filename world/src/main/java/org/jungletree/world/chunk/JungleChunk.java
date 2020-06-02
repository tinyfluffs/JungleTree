package org.jungletree.world.chunk;

import lombok.Value;
import org.jungletree.api.nbt.CompoundTag;
import org.jungletree.api.nbt.Tag;
import org.jungletree.api.world.biome.Biome;
import org.jungletree.api.world.Chunk;
import org.jungletree.api.world.ChunkSection;

import static org.jungletree.api.world.World.CHUNK_SECTION_HEIGHT;

@Value
public class JungleChunk implements Chunk {

    private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;

    int x;
    int z;

    CompoundTag heightMaps;
    Biome[] biomes;
    JungleChunkSection[] sections;
    Tag[] tileEntities;

    public JungleChunk(int x, int z, int height) {
        this.x = x;
        this.z = z;

        this.biomes = new Biome[BIOMES_SIZE];
        this.heightMaps = new CompoundTag();
        this.sections = new JungleChunkSection[height / CHUNK_SECTION_HEIGHT + ((height % CHUNK_SECTION_HEIGHT == 0) ? 0 : 1)];
        this.tileEntities = new Tag[0];

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
