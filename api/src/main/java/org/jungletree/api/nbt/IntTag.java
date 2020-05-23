package org.jungletree.api.nbt;

public final class IntTag extends ValueTag<Integer> {

    public IntTag(Integer value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.INT;
    }

    @Override
    public int size() {
        return Integer.BYTES;
    }
}
