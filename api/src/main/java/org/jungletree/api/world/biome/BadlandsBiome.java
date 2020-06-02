package org.jungletree.api.world.biome;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jungletree.api.world.WeatherType;

@EqualsAndHashCode
public class BadlandsBiome implements Biome {

    private static final String name = "badlands";
    private static final float depth = 0.1f;
    private static final float scale = 0.2f;
    private static final float temperature = 2.0f;
    private static final float downfall = 0.0f;
    private static final int waterColor = 0x3F76E4;
    private static final int waterFogColor = 0x050533;
    private static final Biome parent = null;
    private static final BiomeCategory category = BiomeCategory.MESA;
    private static final WeatherType weatherType = WeatherType.NONE;

    @Getter private final int id;

    BadlandsBiome(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getDepth() {
        return depth;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public float getDownfall() {
        return downfall;
    }

    @Override
    public int getWaterColor() {
        return waterColor;
    }

    @Override
    public int getWaterFogColor() {
        return waterFogColor;
    }

    @Override
    public Biome getParent() {
        return parent;
    }

    @Override
    public BiomeCategory getCategory() {
        return category;
    }

    @Override
    public WeatherType getWeatherType() {
        return weatherType;
    }
}
