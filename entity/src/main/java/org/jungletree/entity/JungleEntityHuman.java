package org.jungletree.entity;

import org.jungletree.api.entity.EntityHuman;

public abstract class JungleEntityHuman extends JungleEntityLiving implements EntityHuman {

    public JungleEntityHuman(int entityId) {
        super(entityId);
    }
}
