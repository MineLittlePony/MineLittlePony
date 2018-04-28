package com.minelittlepony.model.capabilities;

import net.minecraft.entity.Entity;

public interface IModel {

    /**
     * Returns true if this model is on the ground and crouching.
     */
    boolean isCrouching();

    /**
     * Returns true if the given entity can and is flying, or has an elytra.
     */
    boolean isFlying(Entity entity);

    /**
     * Returns true if the model is flying.
     */
    boolean isFlying();

    /**
     * Returns true if the current model is a child or a child-like foal.
     */
    boolean isChild();

    float getSwingAmount();
}
