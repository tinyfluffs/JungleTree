package org.jungletree.api.world.biome;

import lombok.Getter;

public enum BiomeCategory {

    NONE("none"),
    TAIGA("tagia"),
    EXTREME_HILLS("extreme_hills"),
    JUNGLE("jungle"),
    MESA("mesa"),
    PLAINS("plains"),
    SAVANNA("savanna"),
    ICY("icy"),
    THE_END("the_end"),
    BEACH("beach"),
    FOREST("forest"),
    OCEAN("ocean"),
    DESERT("desert"),
    RIVER("river"),
    SWAP("swamp"),
    MUSHROOM("mushroom"),
    NETHER("nether");

    @Getter private final String name;

    BiomeCategory(String name) {
        this.name = name;
    }
}
