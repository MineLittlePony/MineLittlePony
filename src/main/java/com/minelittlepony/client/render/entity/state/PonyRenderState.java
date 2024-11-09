package com.minelittlepony.client.render.entity.state;

import net.minecraft.block.BedBlock;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyModelPrepareCallback;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.*;

public class PonyRenderState extends PlayerEntityRenderState implements PonyModel.AttributedHolder {
    public final ModelAttributes attributes = new ModelAttributes();

    public float vehicleOffset;
    public float riderOffset;
    public float nameplateYOffset;
    public float legOutset;
    public boolean smallArms;
    public boolean sleepingInBed;
    public boolean submergedInWater;
    public boolean onGround;

    public Pony pony;

    public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        attributes.updateLivingState(entity, pony, mode);
        attributes.checkRainboom(entity, model, age);
        this.pony = pony;
        vehicleOffset = hasVehicle ? entity.getVehicle().getEyeHeight(pose) : 0;
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset(entity);
        legOutset = getLegOutset();
        isInSneakingPose = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock;
        submergedInWater = entity.isSubmergedInWater();
        if (attributes.isSitting) {
            pose = EntityPose.SITTING;
        }

        PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(attributes, model, ModelAttributes.Mode.OTHER);
    }

    /**
     * Gets the active scaling profile used to lay out this model's parts.
     */
    public Size getSize() {
        return baby ? SizePreset.FOAL : PonyConfig.getEffectiveSize(attributes.metadata.size());
    }

    public Race getRace() {
        return PonyConfig.getEffectiveRace(attributes.metadata.race());
    }

    public final float getScaleFactor() {
        return getSize().scaleFactor();
    }

    public final float getShadowSize() {
        return getSize().shadowSize();
    }

    /**
     * Gets the current leg swing amount.
     */
    public float getSwingAmount() {
        return this.handSwingProgress;
    }

    /**
     * Gets the step wobble used for various hair bits and animations.
     */
    public float getWobbleAmount() {
        if (getSwingAmount() <= 0) {
            return 0;
        }

        return MathHelper.sin(MathHelper.sqrt(getSwingAmount()) * MathHelper.PI * 2) * 0.04F;
    }

    protected float getLegOutset() {

        float outset = attributes.isLyingDown ? 3.6F : attributes.isCrouching ? 1 : 5;

        if (smallArms) {
            return Math.max(1, outset - 1);
        }
        return outset;
    }

    /**
     * Gets the y-offset applied to entities riding this one.
     */
    protected float getRiderYOffset() {
        switch ((SizePreset)getSize()) {
            case NORMAL: return 0.4F;
            case FOAL:
            case TALL:
            case BULKY:
            default: return 0.25F;
        }
    }

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    public boolean isWearing(Wearable wearable) {
        return isEmbedded(wearable) || attributes.featureSkins.contains(wearable.getId());
    }

    /**
     * Tests if the chosen piece of gear is sourcing its texture from the main skin.
     * i.e. Used to change wing rendering when using saddlebags.
     */
    public boolean isEmbedded(Wearable wearable) {
        return attributes.metadata.gear().matches(wearable);
    }

    private float getNamePlateYOffset(LivingEntity entity) {
        // We start by negating the height calculation done by mahjong.
        float y = -(height + 0.5F);

        // Then we add our own offsets.
        y += attributes.visualHeight * getScaleFactor() + 0.25F;
        y += vehicleOffset;

        if (isInSneakingPose) {
            y -= 0.25F;
        }

        if (isInPose(EntityPose.SLEEPING)) {
            y /= 2;
        }

        return y;
    }

    @Override
    public ModelAttributes getAttributes() {
        return attributes;
    }
}
