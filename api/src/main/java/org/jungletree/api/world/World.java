package org.jungletree.api.world;

import java.util.UUID;

public interface World {

    int CHUNK_SECTION_WIDTH = 16;
    int CHUNK_SECTION_HEIGHT = 16;
    int CHUNK_SECTION_DEPTH = 16;
    int CHUNK_SECTION_SIZE = CHUNK_SECTION_WIDTH * CHUNK_SECTION_HEIGHT * CHUNK_SECTION_DEPTH;

    UUID getId();

    String getName();

    long getSeed();

    int getHeight();

    ChunkGenerator getGenerator();

    Chunk getChunk(int chunkX, int chunkZ);
}
