package org.jungletree.api.entity;

import org.jungletree.api.BlockPositionable;
import org.jungletree.api.Positionable;

public interface Entity extends Positionable, BlockPositionable {

    int getEntityId();

    EntityType getType();
}
