package com.minelittlepony.api.model;

public interface IPegasus extends IModel {

    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen() {
        return (getAttributes().isSwimming || isFlying() || getAttributes().isCrouching) && !getAttributes().isGliding;
    }

    /**
     * Gets the wings of this pegasus/flying creature
     */
    IPart getWings();

    /**
     * Determines angle used to animate wing flaps whilst flying/swimming.
     *
     * @param ticks Partial render ticks
     */
    default float getWingRotationFactor(float ticks) {
        return getAttributes().wingAngle;
    }

}
