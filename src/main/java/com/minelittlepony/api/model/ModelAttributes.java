package com.minelittlepony.api.model;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.common.util.animation.Interpolator;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.Sigma;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

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
     * True if the model is riding on the back of another pony.
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
     * Flag indicating that this model should have a larger than normal head that wobbles.
     */
    public boolean isChibi;
    /**
     * Flag indicating whether the pony is a player
     */
    public boolean isPlayer;
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
     * Flag to indicate whether the wings are open or shut
     */
    public boolean wingsSpread;
    /**
     * Contains a list of additional skins available for rendering.
     */
    public Set<Identifier> featureSkins = new HashSet<>();
    /**
     * Contains the skin metadata associated with this model.
     */
    public PonyData metadata = PonyData.NULL;
    /**
     * The pony's model size.
     *
     * @See com.minelittlepony.api.pony.metadata.SizePreset
     */
    public Size size = SizePreset.NORMAL;
    /**
     * The entity's preferred arm for holding items.
     */
    public HumanoidArm mainArm = HumanoidArm.RIGHT;
    /**
     * The hand currently being swung.
     */
    public InteractionHand activeHand = InteractionHand.MAIN_HAND;

    public int itemUseTime;

    /**
     * A mapping containing any extra data mods want to store for this model.
     */
    public final PatchedDataComponentMap extraData = new PatchedDataComponentMap(DataComponentMap.EMPTY);

    /**
     * The mode the model is being displayed in. i.e First-person, Second-person, or Other-Person
     */
    public Mode displayMode = Mode.OTHER;

    /**
     * Checks flying and speed conditions and sets rainboom to true if we're a species with wings and is going faaast.
     */
    public void checkRainboom(@Nullable LivingEntity entity, PonyModel<?> model, float ticks) {
        Vec3 motion = entity == null ? Vec3.ZERO : entity.getDeltaMovement();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        isGoingFast = (isFlying && model instanceof ModelWithWings) || isGliding;
        isGoingFast &= zMotion > 0.4F;
        isGoingFast |= isRiptide;
        isGoingFast |= isGliding;
        isGoingFast &= entity != null && !entity.isSpectator();

        motionLerp = MathUtil.clampLimit(zMotion * 30, 1);

        wingAngle = calcWingRotationFactor(ticks);
        wingsSpread = (isSwimming || isFlying || isCrouching) && (PonyConfig.getInstance().flappyElytras.get() || !isGliding);
    }

    private float calcWingRotationFactor(float ticks) {
        if (isSwimming) {
            return (Mth.sin(ticks * 0.136f) / 2) + MathUtil.Angles._270_DEG;
        }
        if (isFlying) {
            return Mth.sin(ticks * 0.536f) + ModelWithWings.WINGS_FULL_SPREAD_ANGLE;
        }
        return ModelWithWings.WINGS_RAISED_ANGLE;
    }

    public void updateLivingState(@Nullable LivingEntity entity, Pony pony, Mode mode) {
        if (entity != null) {
            interpolatorId = entity.getUUID();
        }
        displayMode = mode;
        metadata = pony.metadata();
        size = entity != null && entity.isBaby() ? SizePreset.FOAL : pony.size();
        isPlayer = entity instanceof Player;
        visualHeight = (entity == null ? /*Avatar.DEFAULT_EYE_HEIGHT*/ 1.8F : entity.getBbHeight()) + 0.125F;
        isSitting = entity != null && PonyPosture.isSitting(entity);
        isSleeping = entity != null && entity.isAlive() && entity.isSleeping();;
        isLyingDown = isSleeping;
        if (isPlayer) {
            boolean moving = entity.getDeltaMovement().multiply(1, 0, 1).length() == 0 && entity.isCrouching();
            isLyingDown |= getMainInterpolator().interpolate("lyingDown", moving ? 10 : 0, 200) >= 9;
        }

        isCrouching = !isLyingDown && !isSitting && displayMode == Mode.THIRD_PERSON && entity != null && PonyPosture.isCrouching(pony, entity);
        isFlying = !isLyingDown && displayMode == Mode.THIRD_PERSON && entity != null && PonyPosture.isFlying(entity);
        isGliding = entity != null && entity.isFallFlying();
        isSwimming = displayMode == Mode.THIRD_PERSON && entity != null && PonyPosture.isSwimming(entity);
        isRiptide = entity != null && entity.isAutoSpinAttack();
        isRidingInteractive = entity != null && PonyPosture.isRidingAPony(entity);
        isLeftHanded = entity != null && entity.getMainArm() == HumanoidArm.LEFT;
        isHorsey = PonyConfig.getInstance().horsieMode.get();
        isChibi = PonyConfig.getInstance().chibiMode.get();
        featureSkins = entity == null ? Set.of() : SkinsProxy.getInstance().getAvailableSkins(entity);
        mainArm = entity == null ? Minecraft.getInstance().options.mainHand().get() : entity.getMainArm();
        activeHand = entity == null ? InteractionHand.MAIN_HAND : entity.getUsedItemHand();
        itemUseTime = entity == null ? 0 : entity.getUseItemRemainingTicks();
    }

    public Interpolator getMainInterpolator() {
        return Interpolator.linear(interpolatorId);
    }

    public UUID getEntityId() {
        return interpolatorId;
    }

    public boolean shouldLiftArm(ArmPose pose, ArmPose complement, float sigma) {
        return pose != ArmPose.EMPTY
                && (pose != complement || sigma == (isLeftHanded ? Sigma.LEFT : Sigma.RIGHT))
                && (complement != ArmPose.BLOCK && complement != ArmPose.CROSSBOW_HOLD && complement != ArmPose.THROW_TRIDENT);
    }

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    public boolean isWearing(Wearable wearable) {
        return isEmbedded(wearable) || featureSkins.contains(wearable.getId());
    }

    /**
     * Tests if the chosen piece of gear is sourcing its texture from the main skin.
     * i.e. Used to change wing rendering when using saddlebags.
     */
    public boolean isEmbedded(Wearable wearable) {
        return metadata.gear().matches(wearable);
    }

    public enum Mode {
        FIRST_PERSON,
        THIRD_PERSON,
        OTHER
    }
}
