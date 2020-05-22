package org.jungletree.api.world;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Arrays;

@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Material {

    @Getter String name;
    MaterialProperty<? extends Serializable>[] properties;

    public Material(@NonNull String name, MaterialProperty... properties) {
        this.name = name;
        this.properties = properties;
    }

    public MaterialProperty<? extends Serializable>[] getProperties() {
        return Arrays.copyOf(properties, properties.length);
    }
}
