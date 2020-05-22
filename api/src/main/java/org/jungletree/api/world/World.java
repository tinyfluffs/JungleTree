package org.jungletree.api.world;

import java.util.UUID;

public interface World {

    int CHUNK_WIDTH = 16;
    int CHUNK_SECTION_HEIGHT = 16;
    int CHUNK_SECTION_SIZE = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_SECTION_HEIGHT;

    UUID getId();

    String getName();

    long getSeed();

    int getHeight();

    ChunkGenerator getGenerator();

    Chunk getChunk(ChunkPos pos);

    default Chunk getChunk(int chunkX, int chunkZ) {
        return getChunk(new ChunkPos(chunkX, chunkZ));
    }
}
