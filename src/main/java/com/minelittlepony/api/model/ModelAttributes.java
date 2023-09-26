package com.minelittlepony.api.model;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.*;
import com.minelittlepony.common.util.animation.Interpolator;
import com.minelittlepony.util.MathUtil;

import java.util.*;

import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModelAttributes {
    /**
     * True if the model is sleeping in a bed.
     */
    public boolean isSleeping;

    /**
     * True if the model is lying down comfortably
     */
    public boolean isLyingDown;
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
     * Flag indicating that this model should mimic the vanilla horse models.
     */
    public boolean isHorsey;

    /**
     * Vertical pitch whilst flying.
     */
    public float motionPitch;

    /**
     * Horizontal roll whilst flying.
     */
    public float motionRoll;

    /**
     * Lerp amount controlling leg swing whilst performing a rainboom.
     */
    public double motionLerp;

    /**
     * Unique id of the interpolator used for this model.
     * Usually the UUID of the entity being rendered.
     */
    private UUID interpolatorId = UUID.randomUUID();

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
     * Contains a list of additional skins available for rendering.
     */
    public Set<Identifier> featureSkins = new HashSet<>();

    /**
     * Contains the skin metadata associated with this model.
     */
    public PonyData metadata = PonyData.NULL;

    public Arm mainArm;
    public Hand activeHand;
    public ItemStack heldStack = ItemStack.EMPTY;
    public int itemUseTime;

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    public void checkRainboom(LivingEntity entity, PonyModel<?> model, float ticks) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        isGoingFast = (isFlying && model instanceof WingedPonyModel) || isGliding;
        isGoingFast &= zMotion > 0.4F;
        isGoingFast |= entity.isUsingRiptide();
        isGoingFast |= entity.isFallFlying();

        motionLerp = MathUtil.clampLimit(zMotion * 30, 1);

        wingAngle = calcWingRotationFactor(ticks);
    }

    private float calcWingRotationFactor(float ticks) {
        if (isSwimming) {
            return (MathHelper.sin(ticks * 0.136f) / 2) + MathUtil.Angles._270_DEG;
        }
        if (isFlying) {
            return MathHelper.sin(ticks * 0.536f) + WingedPonyModel.WINGS_FULL_SPREAD_ANGLE;
        }
        return WingedPonyModel.WINGS_RAISED_ANGLE;
    }

    public void updateLivingState(LivingEntity entity, Pony pony, Mode mode) {
        visualHeight = entity.getHeight() + 0.125F;
        isSitting = PonyPosture.isSitting(entity);
        isSleeping = entity.isAlive() && entity.isSleeping();;
        isLyingDown = isSleeping;
        isCrouching = !isLyingDown && !isSitting && mode == Mode.THIRD_PERSON && PonyPosture.isCrouching(pony, entity);
        isFlying = !isLyingDown && mode == Mode.THIRD_PERSON && PonyPosture.isFlying(entity);
        isGliding = entity.isFallFlying();
        isSwimming = mode == Mode.THIRD_PERSON && PonyPosture.isSwimming(entity);
        isSwimmingRotated = isSwimming;
        isRiptide = entity.isUsingRiptide();
        isRidingInteractive = PonyPosture.isRidingAPony(entity);
        if (!(entity instanceof PreviewModel)) {
            interpolatorId = entity.getUuid();
        }
        isLeftHanded = entity.getMainArm() == Arm.LEFT;
        isHorsey = PonyConfig.getInstance().horsieMode.get();
        featureSkins = SkinsProxy.instance.getAvailableSkins(entity);
        mainArm = entity.getMainArm();
        activeHand = entity.getActiveHand();
        itemUseTime = entity.getItemUseTimeLeft();
    }

    public Interpolator getMainInterpolator() {
        return Interpolator.linear(interpolatorId);
    }

    public boolean shouldLiftArm(ArmPose pose, ArmPose complement, float sigma) {
        return pose != ArmPose.EMPTY
                && (pose != complement || sigma == (isLeftHanded ? 1 : -1))
                && (complement != ArmPose.BLOCK && complement != ArmPose.CROSSBOW_HOLD);
    }

    public enum Mode {
        FIRST_PERSON,
        THIRD_PERSON,
        OTHER
    }
}
