package org.jungletree.api.nbt;

public final class IntArrayTag extends ValueTag<int[]> {

    public IntArrayTag(int[] value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.INT_ARRAY;
    }

    @Override
    public int size() {
        return Short.BYTES + getValue().length;
    }
}
