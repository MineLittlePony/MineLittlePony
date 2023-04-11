package com.minelittlepony.api.model;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.settings.PonyConfig;

public interface IModel {
    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(BodyPart part, MatrixStack stack);

    /**
     * Gets the active scaling profile used to lay out this model's parts.
     */
    Size getSize();

    /**
     * Gets the transitive properties of this model.
     */
    ModelAttributes getAttributes();

    /**
     * Gets the skin metadata associated with this model.
     */
    default IPonyData getMetadata() {
        return getAttributes().metadata;
    }

    /**
     * Sets the pony metadata object associated with this model.
     */
    void setMetadata(IPonyData meta);

    /**
     * Returns true if the model is flying.
     */
    default boolean isFlying() {
        return getAttributes().isFlying && canFly();
    }

    /**
     * Returns true if this model is riding a boat, horse, or other animals.
     *
     * @deprecated User model#getAttributes().isSitting
     */
    @Deprecated
    default boolean isRiding() {
        return getAttributes().isSitting;
    }

    default Race getRace() {
        return PonyConfig.getEffectiveRace(getMetadata().getRace());
    }

    /**
     * Returns true if this model is being applied to a race that has wings.
     */
    default boolean canFly() {
        return getRace().hasWings();
    }

    /**
     * Gets the current leg swing amount.
     */
    float getSwingAmount();

    /**
     * Gets the step wobble used for various hair bits and animations.
     */
    default float getWobbleAmount() {

        if (getSwingAmount() <= 0) {
            return 0;
        }

        return MathHelper.sin(MathHelper.sqrt(getSwingAmount()) * MathHelper.PI * 2) * 0.04F;
    }

    /**
     * Gets the y-offset applied to entities riding this one.
     */
    float getRiderYOffset();

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    default boolean isWearing(Wearable wearable) {
        return isEmbedded(wearable) || getAttributes().featureSkins.contains(wearable.getId());
    }

    /**
     * Tests if the chosen piece of gear is sourcing its texture from the main skin.
     * i.e. Used to change wing rendering when using saddlebags.
     */
    default boolean isEmbedded(Wearable wearable) {
        return getMetadata().isWearing(wearable);
    }
}
