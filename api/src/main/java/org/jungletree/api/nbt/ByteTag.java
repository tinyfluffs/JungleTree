package org.jungletree.api.nbt;

public final class ByteTag extends ValueTag<Byte> {

    public ByteTag(Byte value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.BYTE;
    }

    @Override
    public int size() {
        return Byte.SIZE;
    }
}
