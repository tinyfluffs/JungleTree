package org.jungletree.api.world;

public interface Chunk {

    int getX();

    int getZ();

    ChunkSection getSection(int index);
}
