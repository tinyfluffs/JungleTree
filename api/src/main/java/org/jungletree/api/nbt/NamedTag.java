package org.jungletree.api.nbt;

public abstract class NamedTag<T> implements Tag<T> {

    private String name;

    public NamedTag(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
