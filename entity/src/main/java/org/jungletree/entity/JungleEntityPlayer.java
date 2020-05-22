package org.jungletree.entity;

import org.jungletree.api.entity.EntityPlayer;
import org.jungletree.api.entity.EntityType;

public class JungleEntityPlayer extends JungleEntityHuman implements EntityPlayer {

    public JungleEntityPlayer(int entityId) {
        super(entityId);
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }
}
