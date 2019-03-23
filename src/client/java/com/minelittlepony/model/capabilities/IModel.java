package com.minelittlepony.model.capabilities;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonySize;
import com.minelittlepony.pony.data.PonyWearable;

public interface IModel extends ICapitated {

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

    void setPitch(float pitch);

    float getPitch();

    /**
     * Gets the active scaling profile used to lay out this model's parts.
     */
    PonySize getSize();

    /**
     * Returns a new pony armour to go with this model. Called on startup by a model wrapper.
     */
    IEquestrianArmour<?> createArmour();

    /**
     * Gets the skin metadata associated with this model.
     */
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
     * Returns true if the model is elytra flying. Elytra flying is different
     * from regular flying in that there are actual "wings" involved.
     */
    boolean isElytraFlying();

    /**
     * Returns true if this model is lying on a bed or bed-like object.
     */
    boolean isSleeping();

    /**
     * Returns true if this model is wimming underwater.
     */
    boolean isSwimming();

    /**
     * Returns true if this model is riding a boat, horse, or other animals.
     */
    boolean isRiding();

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

    /**
     * Gets the current leg swing amount.
     */
    float getSwingAmount();

    /**
     * Gets the step woddle used for various hair bits and animations.
     */
    float getWobbleAmount();

    /**
     * Gets the y-offset applied to entities riding this one.
     */
    float getRiderYOffset();

    /**
     * Gets the actual, visible height of this model when rendered.
     */
    float getModelHeight();

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    default boolean isWearing(PonyWearable wearable) {
        return getMetadata().isWearing(wearable);
    }
}
