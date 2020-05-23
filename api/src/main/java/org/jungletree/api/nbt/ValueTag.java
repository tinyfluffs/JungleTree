package org.jungletree.api.nbt;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
abstract class ValueTag<T> implements Tag {

    @Getter @Setter T value;

    public ValueTag(T value) {
        this.value = value;
    }

    public <V extends Serializable> V as(Class<V> clazz) {
        return (V) value;
    }
}
