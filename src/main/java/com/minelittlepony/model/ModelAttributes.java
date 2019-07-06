package com.minelittlepony.model;

import com.minelittlepony.pony.IPony;
import com.minelittlepony.util.math.MathUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ModelAttributes<T extends LivingEntity> {

    /**
     * True if the model is sleeping in a bed.
     */
    public boolean isSleeping;
    /**
     * True if the model is flying like a pegasus.
     */
    public boolean isFlying;
    /**
     * True if the model is elytra flying. Elytra flying is different
     * from regular flying in that there are actual "wings" involved.
     */
    public boolean isGliding;
    /**
     * True if the model is swimming under water.
     */
    public boolean isSwimming;
    /**
     * True if the pony is crouching.
     */
    public boolean isCrouching;
    /**
     * True if the entity is left-handed.
     */
    public boolean isLeftHanded;
    /**
     * True if the model is sitting as in boats.
     */
    public boolean isSitting;
    /**
     * Flag indicating that this model is performing a rainboom (flight).
     */
    public boolean isGoingFast;
    /**
     * True if the model is wearing any unconventional headgear (ie. a Pumpkin)
     */
    public boolean hasHeadGear;

    /**
     * Vertical pitch whilst flying.
     */
    public float motionPitch;
    /**
     * Lerp amount controlling leg swing whilst performing a rainboom.
     */
    public double motionLerp;

    /**
     * Unique id of the interpolator used for this model.
     * Usually the UUID of the entity being rendered.
     */
    public UUID interpolatorId;

    public int armWidth = 4;
    public int armDepth = 4;

    public int armLength = 12;

    public float armRotationX = 3F;
    public float armRotationY = 8F;

    /**
     * The actual, visible height of this model when rendered.
     * Used when drawing nameplates.
     */
    public float visualHeight = 2F;

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    public void checkRainboom(T entity, float swing, boolean hasWings) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        isGoingFast = (isFlying && hasWings) || isGliding;
        isGoingFast &= zMotion > 0.4F;

        motionLerp = MathUtil.clampLimit(zMotion * 30, 1);
    }

    public void updateLivingState(T entity, IPony pony) {
        isCrouching = pony.isCrouching(entity);
        isSleeping = entity.isSleeping();
        isFlying = pony.isFlying(entity);
        isGliding = entity.isFallFlying();
        isSwimming = pony.isSwimming(entity);
        hasHeadGear = pony.isWearingHeadgear(entity);
        isSitting = pony.isRidingInteractive(entity);
        interpolatorId = entity.getUuid();
        isLeftHanded = entity.getMainHand() == AbsoluteHand.LEFT;
    }
}
