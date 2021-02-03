package com.minelittlepony.model;

import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.armour.IEquestrianArmour;

public interface IModel extends ModelWithArms {

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(BodyPart part, MatrixStack stack);

    /**
     * Gets the active scaling profile used to lay out this model's parts.
     */
    Size getSize();

    /**
     * Returns a new pony armour to go with this model. Called on startup by a model wrapper.
     */
    IEquestrianArmour<?> createArmour();

    /**
     * Gets the transitive properties of this model.
     */
    ModelAttributes<?> getAttributes();

    /**
     * Gets the skin metadata associated with this model.
     */
    IPonyData getMetadata();

    /**
     * Sets the pony metadata object associated with this model.
     */
    void apply(IPonyData meta);

    /**
     * Returns true if the model is flying.
     */
    default boolean isFlying() {
        return getAttributes().isFlying && canFly();
    }

    /**
     * Returns true if this model is riding a boat, horse, or other animals.
     */
    boolean isRiding();

    /**
     * Returns true if this model is being applied to a race that has wings.
     */
    default boolean canFly() {
        return getMetadata().getRace().hasWings();
    }

    /**
     * Returns true if the current model is a child or a child-like foal.
     */
    default boolean isChild() {
        return getSize() == Sizes.FOAL;
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

        return MathHelper.sin(MathHelper.sqrt(getSwingAmount()) * PonyModelConstants.PI * 2) * 0.04F;
    }

    /**
     * Gets the y-offset applied to entities riding this one.
     */
    float getRiderYOffset();

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    default boolean isWearing(Wearable wearable) {
        return getMetadata().isWearing(wearable);
    }
}
