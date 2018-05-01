package com.minelittlepony.ducks;

/**
 * Holding class for entities that support special pony animations used for the renderers.
 */
public interface IPonyAnimationHolder {

    /**
     * Updates and gets the amount this entity is strafing to each side.
     */
    float getStrafeAmount();

    void setStrafeAmount(float strafeAmount);
}
