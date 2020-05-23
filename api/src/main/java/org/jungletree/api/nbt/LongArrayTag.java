package org.jungletree.api.nbt;

public final class LongArrayTag extends ValueTag<long[]> {

    public LongArrayTag(long[] value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.LONG_ARRAY;
    }

    @Override
    public int size() {
        return Short.BYTES + getValue().length;
    }
}
