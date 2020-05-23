package org.jungletree.api.nbt;

public final class EndTag implements Tag {

    @Override
    public TagType type() {
        return TagType.END;
    }

    @Override
    public int size() {
        return 0;
    }
}
