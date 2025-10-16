package com.minelittlepony.client.model;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.mson.util.RenderList;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.MathUtil.Angles;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends PonyRenderState> extends ClientPonyModel<T> {
    public static final float NECK_X = 0.166F;
    public static final float LEG_SNEAKING_PITCH_ADJUSTMENT = 0.4F;
    public static final float BODY_RIDING_PITCH = MathHelper.PI * 3.8F;
    public static final float BODY_SNEAKING_PITCH = 0.4F;
    public static final float FRONT_LEGS_Y = 8;

    public static final Pivot ORIGIN = new Pivot(0, 0, 0);
    public static final Pivot HEAD_SNEAKING = new Pivot(0, 6, -2);
    public static final Pivot BODY_SNEAKING = new Pivot(0, 7, -4);
    public static final Pivot BODY_RIDING = new Pivot(0, 1, 4);
    public static final Pivot FONT_LEGS_SLEEPING = new Pivot(0, 2, 6);
    public static final Pivot BACK_LEGS_SLEEPING = new Pivot(0, 2, -6);

    protected final ModelPart neck;

    protected final RenderList neckRenderList;
    public final RenderList headRenderList;
    protected final RenderList bodyRenderList;

    protected final RenderList legsRenderList;

    protected final RenderList mainRenderList;

    private final List<SubModel<? super T>> parts = new ArrayList<>();

    public AbstractPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);

        neck = tree.getChild("neck");
        mainRenderList = RenderList.of()
            .add(withStage(BodyPart.BODY, bodyRenderList = RenderList.of(body).add(body::applyTransform)))
            .add(withStage(BodyPart.NECK, neckRenderList = RenderList.of(neck)))
            .add(withStage(BodyPart.HEAD, headRenderList = RenderList.of(head)))
            .add(withStage(BodyPart.LEGS, legsRenderList = RenderList.of().add(leftArm, rightArm, leftLeg, rightLeg)));
    }

    protected <P extends SubModel<? super T>> P addPart(P part) {
        parts.add(part);
        return part;
    }

    @Override
    public final void render(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        mainRenderList.accept(stack, vertices, overlay, light, color);
    }

    protected void setModelVisibilities(T state) {
        head.visible = state.headVisible;
        resetPivot(head, neck, leftArm, rightArm, leftLeg, rightLeg);
        hat.visible = head.visible && !state.attributes.isHorsey;
        neck.visible = body.visible;
        if (state.attributes.isHorsey) {
            neck.visible = head.visible;
        } else {
            neck.hidden = !head.visible;
        }
        parts.forEach(part -> part.setVisible(body.visible, state));
    }

    @Override
    protected void setModelAngles(T entity) {
        head.setAngles(entity.pitch * MathHelper.RADIANS_PER_DEGREE, entity.relativeHeadYaw * MathHelper.RADIANS_PER_DEGREE, 0);

        float wobbleAmount = entity.wobbleAmount * getWobbleAmplitude(entity);
        body.yaw = wobbleAmount;
        neck.yaw = wobbleAmount;

        rotateLegs(entity);

        if (!entity.attributes.isSwimming && !entity.attributes.isGoingFast) {
            alignArmForAction(entity, getArm(Arm.LEFT), entity.leftArmPose, entity.rightArmPose, 1);
            alignArmForAction(entity, getArm(Arm.RIGHT), entity.rightArmPose, entity.leftArmPose, -1);
        }
        swingItem(entity);

        if (entity.attributes.isCrouching) {
            ponyCrouch(entity);
        } else if (entity.isInPose(EntityPose.SITTING)) {
            ponySit();
        } else {
            adjustBody(entity, 0, ORIGIN);

            if (!entity.attributes.isLyingDown) {
                animateBreathing(entity);
            }

            if (entity.attributes.isSwimmingRotated) {
                rightLeg.originZ -= 1.5F;
                leftLeg.originZ -= 1.5F;
            }
        }

        if (entity.attributes.isLyingDown) {
            ponySleep();
        }

        if (entity.attributes.isHorsey) {
            head.originY -= 3;
            head.originZ -= 2;
            head.pitch = 0.5F;
        }

        parts.forEach(part -> part.setPartAngles(entity, wobbleAmount));
        mainRenderList.pose(entity);
    }

    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        head.yaw = yaw * MathHelper.RADIANS_PER_DEGREE;
        head.pitch = pitch * MathHelper.RADIANS_PER_DEGREE;
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void ponyCrouch(T state) {
        adjustBody(state, BODY_SNEAKING_PITCH, BODY_SNEAKING);
        HEAD_SNEAKING.set(head);

        rightArm.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        leftArm.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    protected void ponySleep() {
        rightArm.pitch = -MathUtil.Angles._90_DEG;
        leftArm.pitch = -MathUtil.Angles._90_DEG;

        rightLeg.pitch = MathUtil.Angles._90_DEG;
        leftLeg.pitch = MathUtil.Angles._90_DEG;

        FONT_LEGS_SLEEPING.add(rightArm);
        FONT_LEGS_SLEEPING.add(leftArm);
        BACK_LEGS_SLEEPING.add(rightLeg);
        BACK_LEGS_SLEEPING.add(leftLeg);
    }

    protected void ponySit() {
        adjustBodyComponents(BODY_RIDING_PITCH, BODY_RIDING);
        neck.setOrigin(NECK_X, 0, 0);
        head.setOrigin(0, 0, 0);

        leftLeg.originZ = 14;
        leftLeg.originY = 17;
        leftLeg.pitch = -MathHelper.PI / 4;
        leftLeg.yaw = -MathHelper.PI / 7;

        leftLeg.pitch += body.pitch;

        rightLeg.originZ = 15;
        rightLeg.originY = 17;
        rightLeg.pitch = -MathHelper.PI / 4;
        rightLeg.yaw =  MathHelper.PI / 7;

        rightLeg.pitch += body.pitch;

        leftArm.roll = -MathHelper.PI * 0.06f;
        leftArm.pitch += body.pitch;
        rightArm.roll = MathHelper.PI * 0.06f;
        rightArm.pitch += body.pitch;
    }

    /**
    *
    * Used to set the legs rotation based on walking/crouching animations.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void rotateLegs(T state) {
        if (state.attributes.isSwimming) {
            rotateLegsSwimming(state, state.limbAmplitudeInverse, state.limbSwingAmplitude, state.age);
        } else {
            rotateLegsOnGround(state, state.limbAmplitudeInverse, state.limbSwingAmplitude, state.age);
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        rightArm.originZ = 2 + sin;
        leftArm.originZ = 2 - sin;

        float legRPX = state.attributes.getMainInterpolator().interpolate("legOffset", cos - state.legOutset - 0.001F, 2);
        if (state.attributes.isHorsey) {
            legRPX += 2;
        }

        rightArm.originX = -legRPX;
        rightLeg.originX = -legRPX;

        leftArm.originX = legRPX;
        leftLeg.originX = legRPX;

        rightArm.yaw += body.yaw;
        leftArm.yaw += body.yaw;

        if (state.attributes.isHorsey) {
            rightArm.originZ = leftArm.originZ = -1;
            rightArm.originY = leftArm.originY = 6;
            rightLeg.originZ = leftLeg.originZ = 19;
            rightLeg.originY = leftLeg.originY = 6;
        }

        if (state.attributes.isGoingFast) {
            leftLeg.originZ -= 1F;
            rightLeg.originZ -= 1F;
        }
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     */
    protected void rotateLegsSwimming(T state, @Deprecated float move, @Deprecated float swing, @Deprecated float ticks) {
        float lerp = state.isInPose(EntityPose.SWIMMING) ? (float)state.attributes.motionLerp : 1;

        float legLeft = (MathUtil.Angles._90_DEG + MathHelper.sin((state.limbSwingAnimationProgress / 3) + 2 * MathHelper.PI/3) / 2) * lerp;

        float left = (MathUtil.Angles._90_DEG + MathHelper.sin((state.limbSwingAnimationProgress / 3) + 2 * MathHelper.PI) / 2) * lerp;
        float right = (MathUtil.Angles._90_DEG + MathHelper.sin(state.limbSwingAnimationProgress / 3) / 2) * lerp;

        leftArm.setAngles(-left, -left/2, left/2);
        rightArm.setAngles(-right, right/2, -right/2);
        leftLeg.setAngles(legLeft, 0, leftLeg.roll);
        rightLeg.setAngles(right, 0, rightLeg.roll);
    }

    /**
     * Rotates legs in quopy fashion for walking.
     *
     */
    protected void rotateLegsOnGround(T state, float move, float swing, float ticks) {
        float angle = MathHelper.PI * (float) Math.pow(swing, 16);

        float baseRotation = state.limbSwingAnimationProgress * 0.6662F; // magic number ahoy
        float scale = state.limbSwingAmplitude / 4;

        float rainboomLegLotation = state.attributes.getMainInterpolator().interpolate(
                "rainboom_leg_rotation",
                state.attributes.isGoingFast ? 1 : 0,
                5
        );
        float yAngle = 0.2F * rainboomLegLotation;

        leftArm.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, 0);
        rightArm.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI + angle / 2) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, 0);
        leftLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI - (angle * 0.4f)) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, leftLeg.roll);
        rightLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle / 5) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, rightLeg.roll);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        switch (part) {
            default:
            case HEAD: return head;
            case NECK: return neck;
            case TAIL:
            case LEGS:
            case BACK:
            case BODY: return body;
        }
    }

    /**
     * Aligns an arm for the appropriate arm pose
     *
     * @param arm   The arm model to align
     * @param pose  The post to align to
     * @param limbSpeed     Degree to which each 'limb' swings.
     */
    protected void alignArmForAction(T state, ModelPart arm, ArmPose pose, ArmPose complement, float sigma) {
        switch (pose) {
            case ITEM:
                arm.yaw = 0;

                boolean both = pose == complement;

                if (state.attributes.shouldLiftArm(pose, complement, sigma)) {
                    float swag = 1;
                    if (!state.attributes.isFlying && both) {
                        swag -= (float)Math.pow(state.limbSwingAmplitude, 2);
                    }

                    float mult = 1 - swag/2;
                    arm.pitch = arm.pitch * mult - (MathHelper.PI / 10) * swag;
                    arm.roll = -sigma * (MathHelper.PI / 15);
                    arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;

                    if (state.attributes.isCrouching) {
                        arm.originX -= sigma * 2;
                    }
                }

                break;
            case EMPTY:
                arm.yaw = 0;
                break;
            case BLOCK:
                arm.pitch = (arm.pitch / 2 - 0.9424779F) - 0.3F;
                arm.yaw = sigma * MathHelper.PI / 9;
                arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;
                if (complement == pose) {
                    arm.yaw -= sigma * MathHelper.PI / 18;
                }
                arm.originX += sigma;
                arm.originZ += 3;
                if (state.attributes.isCrouching) {
                    arm.originY += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(state, arm);
                break;
            case CROSSBOW_HOLD:
                aimBow(state, arm);

                arm.pitch = head.pitch - MathUtil.Angles._90_DEG;
                arm.yaw = head.yaw + 0.06F;
                break;
            case CROSSBOW_CHARGE:
                aimBow(state, arm);

                arm.pitch = -0.8F;
                arm.yaw = head.yaw + 0.06F;
                arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;
                break;
            case THROW_SPEAR:
                arm.pitch = MathUtil.Angles._90_DEG * 2;
                arm.roll += (0.3F * -state.limbSwingAmplitude + 0.6F) * sigma;
                arm.originY ++;
                break;
            case SPYGLASS:
                float addedPitch = state.isInSneakingPose ? -0.2617994F : 0;
                float minPitch = state.isInSneakingPose ? -1.8F : -2.4F;
                arm.pitch = MathHelper.clamp(head.pitch - 1.9198622F - addedPitch, minPitch, 3.3F);
                arm.yaw = head.yaw;

                if (state.isInSneakingPose) {
                    arm.originY += 9;
                    arm.originX -= 6 * sigma;
                    arm.originZ -= 2;
                }
                if (state.attributes.size == SizePreset.TALL) {
                    arm.originY += 1;
                }
                if (state.attributes.size == SizePreset.FOAL) {
                    arm.originY -= 2;
                }

                break;
            case TOOT_HORN:
                arm.pitch = MathHelper.clamp(head.pitch, -0.55f, 1.2f) - 1.7835298f;
                arm.yaw = head.yaw - 0.1235988f * sigma;
                arm.originY += 3;
                arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;
                break;
            case BRUSH:
                arm.pitch = arm.pitch * 0.5f - 0.62831855f;
                arm.yaw = 0;
                arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;
                break;
            default:
                break;
        }
    }

    protected final void aimBow(T state, ModelPart arm) {
        arm.pitch = MathUtil.Angles._270_DEG + head.pitch + (MathHelper.sin(state.limbSwingAmplitude * 0.067F) * 0.05F);
        arm.yaw = head.yaw - 0.06F;
        arm.roll = MathHelper.cos(state.limbSwingAmplitude * 0.09F) * 0.05F + 0.05F;

        if (state.isInSneakingPose) {
            arm.originY += 4;
        }
    }

    /**
     * Animates arm swinging. Delegates to the correct arm/leg/limb as necessary.
     *
     * @param entity     The entity we are being called for.
     */
    protected final void swingItem(T state) {
        if (state.handSwingProgress > 0 && !state.attributes.isLyingDown) {
            swingArm(state, getArm(state.preferredArm));
        }
    }

    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    protected final void swingArm(T state, ModelPart arm) {
        float swing = 1 - (float)Math.pow(1 - state.handSwingProgress, 3);

        float deltaX = MathHelper.sin(swing * MathHelper.PI);
        float deltaZ = MathHelper.sin(state.handSwingProgress * MathHelper.PI);

        float deltaAim = deltaZ * (0.7F - head.pitch) * 0.75F;

        arm.pitch -= deltaAim + deltaX * 1.2F;
        arm.yaw += body.yaw * 2;
        arm.roll = -deltaZ * 0.4F;
    }

    /**
     * Animates the arm's breathing animation when holding items.
     *
     * @param animationProgress       Total whole and partial ticks since the entity's existence.
     *                    Used in animations together with {@code swing} and {@code move}.
     */
    protected void animateBreathing(T state) {
        float cos = MathHelper.cos(state.age * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(state.age * 0.067F) * 0.05F;

        if (state.attributes.shouldLiftArm(state.rightArmPose, state.leftArmPose, -1)) {
            ModelPart arm = getArm(Arm.RIGHT);
            arm.roll += cos;
            arm.pitch += sin;
        }

        if (state.attributes.shouldLiftArm(state.leftArmPose, state.rightArmPose, 1)) {
            ModelPart arm = getArm(Arm.LEFT);
            arm.roll += cos;
            arm.pitch += sin;
        }
    }

    protected void adjustBody(T state, float pitch, Pivot origin) {
        adjustBodyComponents(pitch, origin);
        if (!state.attributes.isHorsey) {
            neck.setOrigin(NECK_X + pitch, origin.y(), origin.z());
            rightLeg.originY = FRONT_LEGS_Y;
            leftLeg.originY = FRONT_LEGS_Y;
        } else {
            neck.setOrigin(NECK_X + pitch, origin.y() - 1, origin.z() - 2);
            neck.pitch = Angles._30_DEG;
        }
    }

    protected void adjustBodyComponents(float pitch, Pivot origin) {
        body.pitch = pitch;
        body.originY = origin.y();
        body.originZ = origin.z();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        neck.visible = visible;
        hat.visible  = false;
    }

    public void positionheldItem(T state, Arm arm, MatrixStack matrices) {
        float left = arm == Arm.LEFT ? -1 : 1;

        UseAction action = state.getHeldItem(arm).action;

        if (action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0) {
            return;
        }

        matrices.translate(-left * 0.1F, 0.45F, 0);

        if (action == UseAction.BLOCK && state.attributes.itemUseTime == 0) {
            matrices.translate(left * 0.02F, -0.25F, 0);
        }
    }

    @Override
    public void transform(T state, BodyPart part, MatrixStack stack) {

        if (state.attributes.isHorsey) {
            stack.translate(0, 0.1F, 0);
        }

        if (state.attributes.isSleeping || state.attributes.isRiptide) {
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }

        if (state.attributes.isLyingDown && !state.attributes.isSleeping) {
            stack.translate(0, 1.35F, 0);
        }

        if (state.attributes.isHorsey && part == BodyPart.BODY) {
            stack.scale(1.5F, 1, 1.5F);
        }

        PonyTransformation.forSize(state.attributes.size).transform(state.attributes, part, stack);
    }
}
