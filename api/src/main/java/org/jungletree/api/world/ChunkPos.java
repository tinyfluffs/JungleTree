package org.jungletree.api.world;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ChunkPos implements Comparable<ChunkPos> {
    public final int x;
    public final int z;

    private static final int HASH_A = 0x19660D;
    private static final int HASH_C = 0x3C6EF35F;
    private static final int HASH_Z_XOR = 0xDEADBEEF;

    @Override
    public int compareTo(ChunkPos o) {
        if (o == null) return 1;
        if (x < o.x && z < o.z) return -1;
        if (x == o.x && z == o.z) return 0;
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x &&
                z == chunkPos.z;
    }

    @Override
    public int hashCode() {
        final int xTransform = HASH_A * x + HASH_C;
        final int zTransform = HASH_A * (z ^ HASH_Z_XOR) + HASH_C;
        return xTransform ^ zTransform;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + z + "]";
    }
}
