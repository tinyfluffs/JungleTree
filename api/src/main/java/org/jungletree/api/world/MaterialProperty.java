package org.jungletree.api.world;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class MaterialProperty<T extends Serializable> {
    @NonNull String name;
    @NonNull T value;
}
