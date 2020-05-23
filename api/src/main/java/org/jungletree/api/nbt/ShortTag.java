package org.jungletree.api.nbt;

public final class ShortTag extends ValueTag<Short> {

    public ShortTag(Short value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.SHORT;
    }

    @Override
    public int size() {
        return Short.BYTES;
    }
}
