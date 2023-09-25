package com.minelittlepony.api.model;

import net.minecraft.entity.LivingEntity;

public interface HornedPonyModel<T extends LivingEntity> extends PonyModel<T> {
    /**
     * Returns true if this model is being applied to a race that can use magic.
     */
    default boolean hasMagic() {
        return getRace().hasHorn() && getAttributes().metadata.glowColor() != 0;
    }

    /**
     * Returns true if this model is currently using magic (horn is lit).
     */
    boolean isCasting();
}
