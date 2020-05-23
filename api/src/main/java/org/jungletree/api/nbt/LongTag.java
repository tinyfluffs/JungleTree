package org.jungletree.api.nbt;

public final class LongTag extends ValueTag<Long> {

    public LongTag(Long value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.LONG;
    }

    @Override
    public int size() {
        return Long.BYTES;
    }
}
