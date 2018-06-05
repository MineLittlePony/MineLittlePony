package com.minelittlepony.model.capabilities;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyWearable;

public interface IModel {

    /**
     * Sets up this model's initial values, like a constructor...
     * @param yOffset   YPosition for this model. Always 0.
     * @param stretch   Scaling factor for this model. Ranges above or below 0 (no change).
     */
    void init(float yOffset, float stretch);

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(BodyPart part);


    /**
     * Returns a new pony armour to go with this model. Called on startup by a model wrapper.
     */
    PonyArmor createArmour();

    IPonyData getMetadata();

    /**
     * Returns true if this model is on the ground and crouching.
     */
    boolean isCrouching();

    /**
     * Returns true if the model is flying.
     */
    boolean isFlying();

    /**
     * Returns true if this model is wimming underwater.
     */
    boolean isSwimming();

    /**
     * Returns true if we're flying really fast.
     */
    boolean isGoingFast();

    /**
     * Returns true if this model is being applied to a race that has wings.
     */
    default boolean canFly() {
        return getMetadata().getRace().hasWings();
    }

    /**
     * Returns true if the current model is a child or a child-like foal.
     */
    boolean isChild();

    float getSwingAmount();

    default boolean isWearing(PonyWearable wearable) {
        return getMetadata().isWearing(wearable);
    }
}
