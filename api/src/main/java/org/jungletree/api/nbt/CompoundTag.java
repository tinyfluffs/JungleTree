package org.jungletree.api.nbt;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CompoundTag extends ValueTag<Map<String, Tag>> {

    public CompoundTag() {
        super(new LinkedHashMap<>());
    }

    public CompoundTag(Map<String, Tag> value) {
        super(value);
    }

    @Override
    public TagType type() {
        return TagType.COMPOUND;
    }

    @Override
    public int size() {
        int size = 0;
        size += Byte.BYTES;
        for (Map.Entry<String, Tag> e : getValue().entrySet()) {
            size += Short.BYTES;
            size += e.getKey().length();
            size += e.getValue().size();
        }
        return size;
    }
}
