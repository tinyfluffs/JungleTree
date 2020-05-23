package org.jungletree.api.world;

import java.util.*;

public final class Palette {

    public static final int GLOBAL_PALETTE_BITS_PER_BLOCK = 14;

    private static final String DEFAULT_PREFIX = "minecraft:";
    private static final String DEFAULT_MATERIAL_NAME = DEFAULT_PREFIX + "air";
    private static final BlockState DEFAULT_BLOCK_STATE = new BlockState(DEFAULT_MATERIAL_NAME);

    private final Map<Character, BlockState> materials;

    public Palette(BlockState... m) {
        int pos = 0;
        this.materials = new TreeMap<>();
        this.materials.put((char) pos++, DEFAULT_BLOCK_STATE);
        for (BlockState blockState : m) {
            this.materials.put((char) pos++, blockState);
        }
    }

    public char getState(BlockState blockState) {
        for (Map.Entry<Character, BlockState> e : materials.entrySet()) {
            if (e.getValue().equals(blockState)) {
                return e.getKey();
            }
        }
        return 0;
    }

    public BlockState fromId(char id) {
        return materials.getOrDefault(id, DEFAULT_BLOCK_STATE);
    }

    public BlockState fromName(String name) {
        name = name.toLowerCase();
        for (BlockState m : materials.values()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return fromId((char) 0);
    }

    @Override
    public String toString() {
        return materials.toString();
    }
}
