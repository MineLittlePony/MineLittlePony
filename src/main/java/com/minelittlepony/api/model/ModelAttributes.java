package com.minelittlepony.api.model;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.util.MathUtil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

import static com.minelittlepony.api.model.PonyModelConstants.ROTATE_270;
import static com.minelittlepony.api.model.PonyModelConstants.WING_ROT_Z_SNEAK;
import static com.minelittlepony.api.model.PonyModelConstants.WING_ROT_Z_FLYING;

public class ModelAttributes {
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
     * True if the model is rotated 90degs (players)
     */
    public boolean isHorizontal;
    /**
     * True if the model is using riptide (players)
     */
    public boolean isRiptide;
    /**
     * True if the model is swimming under water.
     */
    public boolean isSwimming;
    /**
     * True if the model is swimming, and rotated 90degs (players)
     */
    public boolean isSwimmingRotated;

    /**
     * True if the pony is crouching.
     */
    public boolean isCrouching;
    /**
     * True if the pony is sitting.
     */
    public boolean isSitting;

    /**
     * True if the entity is left-handed.
     */
    public boolean isLeftHanded;
    /**
     * True if the model is sitting as in boats.
     */
    public boolean isRidingInteractive;
    /**
     * Flag indicating that this model is performing a rainboom (flight).
     */
    public boolean isGoingFast;

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
    public UUID interpolatorId = UUID.randomUUID();

    /**
     * The actual, visible height of this model when rendered.
     * Used when drawing name plates.
     */
    public float visualHeight = 2F;

    /**
     * The angle used to animate wing flaps whilst flying/swimming.
     */
    public float wingAngle;

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    public void checkRainboom(LivingEntity entity, float swing, boolean hasWings, float ticks) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        isGoingFast = (isFlying && hasWings) || isGliding;
        isGoingFast &= zMotion > 0.4F;
        isGoingFast |= entity.isUsingRiptide();

        motionLerp = MathUtil.clampLimit(zMotion * 30, 1);

        wingAngle = calcWingRotationFactor(ticks);
    }

    private float calcWingRotationFactor(float ticks) {
        if (isSwimming) {
            return (MathHelper.sin(ticks * 0.136f) / 2) + ROTATE_270;
        }
        if (isFlying) {
            return MathHelper.sin(ticks * 0.536f) + WING_ROT_Z_FLYING;
        }
        return WING_ROT_Z_SNEAK;
    }

    public void updateLivingState(LivingEntity entity, IPony pony, Mode mode) {
        visualHeight = entity.getHeight() + 0.125F;
        isSitting = pony.isSitting(entity);
        isCrouching = !isSitting && mode == Mode.THIRD_PERSON && pony.isCrouching(entity);
        isSleeping = entity.isSleeping();
        isFlying = mode == Mode.THIRD_PERSON && pony.isFlying(entity);
        isGliding = entity.isFallFlying();
        isSwimming = mode == Mode.THIRD_PERSON && pony.isSwimming(entity);
        isSwimmingRotated = isSwimming && (entity instanceof PlayerEntity || entity instanceof Swimmer);
        isRiptide = entity.isUsingRiptide();
        isHorizontal = isSwimming;
        isRidingInteractive = pony.isRidingInteractive(entity);
        interpolatorId = entity.getUuid();
        isLeftHanded = entity.getMainArm() == Arm.LEFT;
    }

    public enum Mode {
        FIRST_PERSON,
        THIRD_PERSON,
        OTHER
    }

    /**
     * Special interface to mark entities that rotate horizontally when they swim.
     */
    public interface Swimmer {
    }
}
