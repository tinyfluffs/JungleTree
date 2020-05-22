package org.jungletree.entity;

import lombok.Getter;
import lombok.Setter;
import org.jungletree.api.entity.Entity;

public abstract class JungleEntity implements Entity {

    private final int entityId;
    @Getter @Setter private float x, y, z;

    public JungleEntity(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    @Override
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    @Override
    public int getBlockZ() {
        return (int) Math.floor(z);
    }
}
