package org.jungletree.api.nbt;

public final class StringTag extends ValueTag<String> {

    public StringTag(String value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.STRING;
    }

    @Override
    public int size() {
        return Short.BYTES + getValue().length();
    }
}
