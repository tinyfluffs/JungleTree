package org.jungletree.api.world;

import java.util.*;

public final class Palette {

    private static final String DEFAULT_PREFIX = "minecraft:";
    private static final String DEFAULT_MATERIAL_NAME = DEFAULT_PREFIX + "air";
    private static final Material DEFAULT_MATERIAL = new Material(DEFAULT_MATERIAL_NAME);

    private final Map<Character, Material> materials;

    public Palette(Material... m) {
        int pos = 0;
        this.materials = new TreeMap<>();
        this.materials.put((char) pos++, DEFAULT_MATERIAL);
        for (Material material : m) {
            this.materials.put((char) pos++, material);
        }
    }

    public char getId(Material material) {
        for (Map.Entry<Character, Material> e : materials.entrySet()) {
            if (e.getValue().equals(material)) {
                return e.getKey();
            }
        }
        return 0;
    }

    public Material fromId(char id) {
        return materials.getOrDefault(id, DEFAULT_MATERIAL);
    }

    public Material fromName(String name) {
        name = name.toLowerCase();
        for (Material m : materials.values()) {
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
