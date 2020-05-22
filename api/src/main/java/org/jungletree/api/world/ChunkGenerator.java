package org.jungletree.api.world;

public interface ChunkGenerator {

    String getName();

    void generate(Chunk chunk, ChunkPos pos);
}
