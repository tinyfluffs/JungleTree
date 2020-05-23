package org.jungletree.api.nbt;

import java.io.Serializable;

public interface Tag extends Serializable {

    TagType type();

    int size();
}
