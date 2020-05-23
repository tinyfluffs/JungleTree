package org.jungletree.api.nbt;

public final class DoubleTag extends ValueTag<Double> {

    public DoubleTag(Double value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.DOUBLE;
    }

    @Override
    public int size() {
        return Double.BYTES;
    }
}
