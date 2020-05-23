package org.jungletree.api.nbt;

import java.util.List;

public final class ListTag<T extends Tag> extends ValueTag<List<T>> {

    private final TagType itemType;

    public ListTag(TagType itemType, List<T> value) {
        super(value);
        this.itemType = itemType;
    }

    public TagType itemType() {
        return itemType;
    }

    @Override
    public TagType type() {
        return TagType.LIST;
    }

    @Override
    public int size() {
        return getValue().size() == 0 ? 0 : 1 + Integer.BYTES;
    }
}
