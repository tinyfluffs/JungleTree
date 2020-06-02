package org.jungletree.api.world.biome;

import org.jungletree.api.world.WeatherType;

import java.util.Optional;

public enum BiomeType {

    BADLANDS(37, new BadlandsBiome(37));

    private final int id;
    private final Biome type;

    BiomeType(int id, Biome biome) {
        this.id = id;
        this.type = biome;
    }

    public static Optional<BiomeType> fromId(int id) {
        for (BiomeType v : values()) {
            if (v.id == id) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public Biome get() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return type.getName();
    }

    public float getDepth() {
        return type.getDepth();
    }

    public float getScale() {
        return type.getScale();
    }

    public float getTemperature() {
        return type.getTemperature();
    }

    public float getDownfall() {
        return type.getDownfall();
    }

    public int getWaterColor() {
        return type.getWaterColor();
    }

    public int getWaterFogColor() {
        return type.getWaterFogColor();
    }

    public Biome getParent() {
        return type.getParent();
    }

    public BiomeCategory getCategory() {
        return type.getCategory();
    }

    public WeatherType getWeatherType() {
        return type.getWeatherType();
    }
}
