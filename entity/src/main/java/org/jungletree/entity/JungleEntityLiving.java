package org.jungletree.entity;

import org.jungletree.api.entity.EntityLiving;

public abstract class JungleEntityLiving extends JungleEntity implements EntityLiving {

    public JungleEntityLiving(int entityId) {
        super(entityId);
    }
}
