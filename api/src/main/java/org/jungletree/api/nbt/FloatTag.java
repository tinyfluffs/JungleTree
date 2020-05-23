package org.jungletree.api.nbt;

public final class FloatTag extends ValueTag<Float> {

    public FloatTag(Float value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.FLOAT;
    }

    @Override
    public int size() {
        return Float.BYTES;
    }
}
