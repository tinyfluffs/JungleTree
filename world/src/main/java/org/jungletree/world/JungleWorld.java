package org.jungletree.world;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.world.Chunk;
import org.jungletree.api.world.ChunkGenerator;
import org.jungletree.api.world.World;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JungleWorld implements World {

    @Getter UUID id;
    @Getter String name;
    @Getter long seed;
    @Getter int height;
    @Getter ChunkGenerator generator;
    ChunkLoader chunkLoader;

    public JungleWorld(UUID id, String name, long seed, int height, ChunkGenerator generator) {
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.height = height;
        this.generator = generator;
        this.chunkLoader = new ChunkLoader(this);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return chunkLoader.getOrGenerate(chunkX, chunkZ);
    }
}
