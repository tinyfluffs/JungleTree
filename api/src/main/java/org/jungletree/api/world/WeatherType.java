package org.jungletree.api.world;

import lombok.Getter;

public enum WeatherType {

    NONE("none"), RAIN("rain"), SNOW("snow");

    @Getter private final String name;

    WeatherType(String name) {
        this.name = name;
    }
}
