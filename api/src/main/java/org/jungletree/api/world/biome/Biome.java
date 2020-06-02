package org.jungletree.api.world.biome;

import org.jungletree.api.util.ExtendedMath;
import org.jungletree.api.world.WeatherType;

public interface Biome {

    int getId();

    String getName();

    float getDepth();

    float getScale();

    float getTemperature();

    float getDownfall();

    int getWaterColor();

    int getWaterFogColor();

    default int getSkyColor() {
        float color = this.getTemperature() / 3.0f;
        color = ExtendedMath.clamp(color, -1.0f, 1.0f);
        return ExtendedMath.HSBtoRGB(0.62222224f - color * 0.05f, 0.5f + color * 0.1f, 1.0f);
    }

    Biome getParent();

    BiomeCategory getCategory();

    WeatherType getWeatherType();
}
