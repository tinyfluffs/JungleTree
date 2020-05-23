package org.jungletree.api.nbt;

public final class ByteArrayTag extends ValueTag<byte[]> {

    public ByteArrayTag(byte[] value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    public int size() {
        return Short.BYTES + getValue().length;
    }
}
