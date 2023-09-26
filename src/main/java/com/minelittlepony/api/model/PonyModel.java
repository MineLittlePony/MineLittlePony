package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.mson.api.MsonModel;

public interface PonyModel<T extends LivingEntity> extends MsonModel, ModelWithHooves, ModelWithHat, ModelWithHead {

    void copyAttributes(BipedEntityModel<T> other);

    void updateLivingState(T entity, Pony pony, ModelAttributes.Mode mode);

    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(BodyPart part, MatrixStack stack);

    /**
     * Gets the transitive properties of this model.
     */
    ModelAttributes getAttributes();

    /**
     * Sets the pony metadata object associated with this model.
     */
    void setMetadata(PonyData meta);

    /**
     * Gets the active scaling profile used to lay out this model's parts.
     */
    default Size getSize() {
        return PonyConfig.getEffectiveSize(getAttributes().metadata.size());
    }

    default Race getRace() {
        return PonyConfig.getEffectiveRace(getAttributes().metadata.race());
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
        return getAttributes().metadata.gear().matches(wearable);
    }

}
