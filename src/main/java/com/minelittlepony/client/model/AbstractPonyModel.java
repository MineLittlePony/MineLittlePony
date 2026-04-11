package com.minelittlepony.client.model;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.common.util.Untyped;
import com.minelittlepony.mson.util.RenderList;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.MathUtil.Angles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;

import org.joml.Quaternionf;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends PonyRenderState> extends ClientPonyModel<T> {
    public static final float LEG_SNEAKING_PITCH_ADJUSTMENT = 0.4F;
    public static final float BODY_RIDING_PITCH = Mth.PI * 3.8F;
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
    public final RenderList bodyRenderList;

    public final RenderList legsRenderList;

    protected final RenderList mainRenderList;

    private final List<SubModel<? super T>> parts = new ArrayList<>();

    public AbstractPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);

        neck = tree.getChild("neck");
        mainRenderList = RenderList.of()
            .add(bodyRenderList = withStage(BodyPart.BODY).add(body).add(body::translateAndRotate))
            .add(neckRenderList = withStage(BodyPart.NECK).add(neck))
            .add(headRenderList = withStage(BodyPart.HEAD).add(head))
            .add(legsRenderList = withStage(BodyPart.LEGS).add(leftArm, rightArm, leftLeg, rightLeg));
    }

    protected <P extends SubModel<? super T>> P addPart(P part) {
        parts.add(part);
        return part;
    }

    @Override
    public final void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        mainRenderList.accept(matrices, vertices, light, overlay, color);
    }

    public final void renderHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        headRenderList.accept(matrices, vertices, light, overlay, color);
    }

    @Override
    protected void setModelVisibilities(T state) {
        head.visible = state.headVisible;
        hat.visible = head.visible && !state.attributes.isHorsey;
        neck.visible = body.visible;
        if (state.attributes.isHorsey) {
            neck.visible = head.visible;
        } else {
            neck.skipDraw = !head.visible;
        }
        parts.forEach(part -> part.setVisible(body.visible, state));
    }

    @Override
    protected void setModelAngles(T entity) {
        resetPose();
        head.setRotation(entity.xRot * Mth.DEG_TO_RAD, entity.yRot * Mth.DEG_TO_RAD, 0);

        body.yRot = entity.wobbleAmount;
        neck.yRot = entity.wobbleAmount;

        rotateLegs(entity);

        if (!entity.attributes.isSwimming && !entity.attributes.isGoingFast) {
            alignArmForAction(entity, getArm(HumanoidArm.LEFT), entity.leftArmPose, entity.rightArmPose, 1);
            alignArmForAction(entity, getArm(HumanoidArm.RIGHT), entity.rightArmPose, entity.leftArmPose, -1);
        }
        if (entity.attackTime > 0 && !entity.attributes.isLyingDown) {
            swingArm(entity, getArm(entity.mainArm));
        }

        if (entity.attributes.isCrouching) {
            ponyCrouch(entity);
        } else if (entity.hasPose(Pose.SITTING)) {
            ponySit();
        } else {
            adjustBody(entity, 0, ORIGIN);

            if (!entity.attributes.isLyingDown) {
                animateBreathing(entity);
            }
        }

        if (entity.attributes.isLyingDown) {
            ponySleep();
        }

        if (entity.attributes.isHorsey) {
            head.y -= 3;
            head.z -= 2;
            head.xRot = 0.5F;
        }

        if (PonyConfig.getInstance().chibiMode.get()) {
            head.xScale += 0.5;
            head.zScale += 0.5;
            head.yScale += 0.5;
            float bobScale = entity.attributes.getMainInterpolator().interpolate("head_bob", entity.walkAnimationPos, 120) * 0.4F;
            head.zRot += Mth.sin(entity.ageInTicks / 2F) * bobScale;
            head.yRot += Mth.sin(entity.ageInTicks / 3F) * bobScale;
            head.xRot += Mth.cos(entity.ageInTicks / 2F) * bobScale * 1.2F;
        }

        parts.forEach(part -> part.setAngles(Untyped.cast(this), entity));
        mainRenderList.pose(entity);
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void ponyCrouch(T state) {
        adjustBody(state, BODY_SNEAKING_PITCH, BODY_SNEAKING);
        HEAD_SNEAKING.set(head);

        rightArm.xRot -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        leftArm.xRot -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    protected void ponySleep() {
        rightArm.xRot = -MathUtil.Angles._90_DEG;
        leftArm.xRot = -MathUtil.Angles._90_DEG;

        rightLeg.xRot = MathUtil.Angles._90_DEG;
        leftLeg.xRot = MathUtil.Angles._90_DEG;

        FONT_LEGS_SLEEPING.add(rightArm);
        FONT_LEGS_SLEEPING.add(leftArm);
        BACK_LEGS_SLEEPING.add(rightLeg);
        BACK_LEGS_SLEEPING.add(leftLeg);
    }

    protected void ponySit() {
        adjustBodyComponents(BODY_RIDING_PITCH, BODY_RIDING);
        neck.setPos(0, 0, 0);
        head.setPos(0, 0, 0);

        leftLeg.z = 14;
        leftLeg.y = 17;
        leftLeg.xRot = -Mth.PI / 4;
        leftLeg.yRot = -Mth.PI / 7;

        leftLeg.xRot += body.xRot;

        rightLeg.z = 15;
        rightLeg.y = 17;
        rightLeg.xRot = -Mth.PI / 4;
        rightLeg.zRot =  Mth.PI / 7;

        rightLeg.xRot += body.xRot;

        leftArm.zRot = -Mth.PI * 0.06f;
        leftArm.xRot += body.xRot;
        rightArm.zRot = Mth.PI * 0.06f;
        rightArm.xRot += body.xRot;
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
            rotateLegsSwimming(state, state.speedValue, state.walkAnimationPos, state.ageInTicks);
        } else {
            rotateLegsOnGround(state, state.speedValue, state.walkAnimationPos, state.ageInTicks);
        }

        float cos = Mth.cos(body.yRot) * 5;

        float legRPX = state.attributes.getMainInterpolator().interpolate("legOffset", cos - state.legOutset - 0.001F, 2);
        if (state.attributes.isHorsey) {
            legRPX += 2;
        }

        rightArm.x = -legRPX;
        rightLeg.x = -legRPX;

        leftArm.x = legRPX;
        leftLeg.x = legRPX;

        rightArm.yRot += body.yRot;
        leftArm.yRot += body.yRot;

        if (state.attributes.isHorsey) {
            rightArm.z = leftArm.z = -1;
            rightArm.y = leftArm.y = 6;
            rightLeg.z = leftLeg.z = 19;
            rightLeg.y = leftLeg.y = 6;
        }
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     */
    protected void rotateLegsSwimming(T state, @Deprecated float move, @Deprecated float swing, @Deprecated float ticks) {
        float lerp = state.submergedInWater ? (float)state.attributes.motionLerp : 1;

        float legLeft = (MathUtil.Angles._90_DEG + Mth.sin((state.walkAnimationSpeed / 3) + 2 * Mth.PI / 3) / 2) * lerp;

        float left = (MathUtil.Angles._90_DEG + Mth.sin((state.walkAnimationSpeed / 3) + 2 * Mth.PI) / 2) * lerp;
        float right = (MathUtil.Angles._90_DEG + Mth.sin(state.walkAnimationSpeed / 3) / 2) * lerp;

        leftArm.setRotation(-left, -left / 2, left / 2);
        rightArm.setRotation(-right, right / 2, -right / 2);
        leftLeg.setRotation(legLeft, 0, leftLeg.zRot);
        rightLeg.setRotation(right, 0, rightLeg.zRot);
    }

    /**
     * Rotates legs in quopy fashion for walking.
     *
     */
    protected void rotateLegsOnGround(T state, float move, float swing, float ticks) {
        float angle = Mth.PI * (float) Math.pow(swing, 16);

        float baseRotation = state.walkAnimationSpeed * 0.6662F; // magic number ahoy
        float scale = state.walkAnimationPos / 4;

        float rainboomLegLotation = state.attributes.getMainInterpolator().interpolate(
                "rainboom_leg_rotation",
                state.attributes.isGoingFast ? 1 : 0,
                5
        );
        float yAngle = 0.2F * rainboomLegLotation;

        leftArm.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + angle) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, 0);
        rightArm.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + Mth.PI + angle / 2) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, 0);
        leftLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + Mth.PI - (angle * 0.4f)) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, leftLeg.zRot);
        rightLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + angle / 5) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, rightLeg.zRot);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        if (part == BodyPart.NECK) {
            return neck;
        }
        return super.getBodyPart(part);
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
                arm.yRot = 0;

                boolean both = pose == complement;

                if (state.attributes.shouldLiftArm(pose, complement, sigma)) {
                    float swag = 1;
                    if (!state.attributes.isFlying && both) {
                        swag -= (float)Math.pow(state.walkAnimationPos, 2);
                    }

                    float mult = 1 - swag/2;
                    arm.xRot = arm.xRot * mult - (Mth.PI / 10) * swag;
                    arm.zRot = -sigma * (Mth.PI / 15);
                    arm.zRot += 0.3F * -state.walkAnimationPos * sigma;

                    if (state.attributes.isCrouching) {
                        arm.x -= sigma * 2;
                    }
                }

                break;
            case EMPTY:
                arm.yRot = 0;
                break;
            case BLOCK:
                arm.xRot = (arm.xRot / 2 - 0.9424779F) - 0.3F;
                arm.yRot = sigma * Mth.PI / 9;
                arm.zRot += 0.3F * -state.walkAnimationPos * sigma;
                if (complement == pose) {
                    arm.yRot -= sigma * Mth.PI / 18;
                }
                arm.x += sigma;
                arm.z += 3;
                if (state.attributes.isCrouching) {
                    arm.y += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(state, arm);
                break;
            case CROSSBOW_HOLD:
                aimBow(state, arm);

                arm.zRot = head.zRot - MathUtil.Angles._90_DEG;
                arm.yRot = head.yRot + 0.06F;
                break;
            case CROSSBOW_CHARGE:
                aimBow(state, arm);

                arm.xRot = -0.8F;
                arm.yRot = head.yRot + 0.06F;
                arm.zRot += 0.3F * -state.walkAnimationPos * sigma;
                break;
            case THROW_TRIDENT:
                arm.xRot = MathUtil.Angles._90_DEG * 2;
                arm.zRot += (0.3F * -state.walkAnimationPos + 0.6F) * sigma;
                arm.y ++;
                break;
            case SPYGLASS:
                float addedPitch = state.isCrouching ? -0.2617994F : 0;
                float minPitch = state.isCrouching ? -1.8F : -2.4F;
                arm.xRot = Mth.clamp(head.xRot - 1.9198622F - addedPitch, minPitch, 3.3F);
                arm.yRot = head.yRot;

                if (state.isCrouching) {
                    arm.y += 9;
                    arm.x -= 6 * sigma;
                    arm.z -= 2;
                }
                if (state.attributes.size == SizePreset.TALL) {
                    arm.y += 1;
                }
                if (state.attributes.size == SizePreset.FOAL) {
                    arm.y -= 2;
                }

                break;
            case TOOT_HORN:
                arm.xRot = Mth.clamp(head.xRot, -0.55f, 1.2f) - 1.7835298f;
                arm.yRot = head.yRot - 0.1235988f * sigma;
                arm.y += 3;
                arm.zRot += 0.3F * -state.walkAnimationPos * sigma;
                break;
            case BRUSH:
                arm.xRot = arm.xRot * 0.5f - 0.62831855f;
                arm.yRot = 0;
                arm.zRot += 0.3F * -state.walkAnimationPos * sigma;
                break;
            case SPEAR:
                SpearAnimations.thirdPersonHandUse(arm, head, sigma > 0, state.getUseItemStackForArm(sigma > 0 ? HumanoidArm.RIGHT : HumanoidArm.LEFT), state);
                break;
            default:
                break;
        }
    }

    protected final void aimBow(T state, ModelPart arm) {
        arm.xRot = MathUtil.Angles._270_DEG + head.xRot + (Mth.sin(state.walkAnimationPos * 0.067F) * 0.05F);
        arm.yRot = head.yRot - 0.06F;
        arm.zRot = Mth.cos(state.walkAnimationPos * 0.09F) * 0.05F + 0.05F;

        if (state.isCrouching) {
            arm.y += 4;
        }
    }

    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    protected void swingArm(T state, ModelPart arm) {
        float swing = 1 - (float)Math.pow(1 - state.attackTime, 3);

        float deltaX = Mth.sin(swing * Mth.PI);
        float deltaZ = Mth.sin(state.attackTime * Mth.PI);

        float deltaAim = deltaZ * (0.7F - head.xRot) * 0.75F;

        arm.xRot -= deltaAim + deltaX * 1.2F;
        arm.yRot += body.yRot * 2;
        arm.zRot = -deltaZ * 0.4F;
    }

    /**
     * Animates the arm's breathing animation when holding items.
     *
     * @param animationProgress       Total whole and partial ticks since the entity's existence.
     *                    Used in animations together with {@code swing} and {@code move}.
     */
    protected void animateBreathing(T state) {
        float cos = Mth.cos(state.ageInTicks * 0.09F) * 0.05F + 0.05F;
        float sin = Mth.sin(state.ageInTicks * 0.067F) * 0.05F;

        if (state.attributes.shouldLiftArm(state.rightArmPose, state.leftArmPose, -1)) {
            ModelPart arm = getArm(HumanoidArm.RIGHT);
            arm.zRot += cos;
            arm.xRot += sin;
        }

        if (state.attributes.shouldLiftArm(state.leftArmPose, state.rightArmPose, 1)) {
            ModelPart arm = getArm(HumanoidArm.LEFT);
            arm.zRot += cos;
            arm.xRot += sin;
        }
    }

    protected void adjustBody(T state, float pitch, Pivot origin) {
        adjustBodyComponents(pitch, origin);
        if (!state.attributes.isHorsey) {
            neck.setPos(0, origin.y(), origin.z());
            rightLeg.y = FRONT_LEGS_Y;
            leftLeg.y = FRONT_LEGS_Y;
        } else {
            neck.setPos(0, origin.y() - 1, origin.z() - 2);
            neck.xRot = Angles._30_DEG;
        }
    }

    protected void adjustBodyComponents(float pitch, Pivot origin) {
        body.xRot = pitch;
        body.y = origin.y();
        body.z = origin.z();
    }

    public final void transformHeldItem(T state, HumanoidArm arm, PoseStack matrices) {
        transform(state, BodyPart.LEGS, matrices);
        ModelPart a = getArm(arm);
        Quaternionf rotation = new Quaternionf().rotationZYX(a.zRot, a.yRot, a.xRot);
        matrices.mulPose(rotation);
        positionheldItem(state, arm, matrices);
        matrices.mulPose(rotation.conjugate());
    }

    protected void positionheldItem(T state, HumanoidArm arm, PoseStack matrices) {
        float left = arm == HumanoidArm.LEFT ? -1 : 1;
        ArmPose pose = arm == HumanoidArm.LEFT ? state.leftArmPose : state.rightArmPose;

        if (pose == ArmPose.SPYGLASS) {
            matrices.translate(0, 0.3, 0.3);
            return;
        }

        matrices.translate(-left * 0.06F, 0.355F, -0.06F);

        if (pose == ArmPose.BOW_AND_ARROW) {
            matrices.translate(0, 0.1F, 0);
        }
    }

    @Override
    public void transform(T state, BodyPart part, PoseStack stack) {
        float originY = 1.5F;
        float originZ = 0;

        if (state.attributes.isSleeping || state.attributes.isRiptide) {
            originY += -0.7F;
        }

        if (state.attributes.isLyingDown) {
            originZ += 0.5F;
        }

        if (part != BodyPart.WINGS) {
            if (state.attributes.isSleeping || state.attributes.isRiptide) {
                stack.mulPose(Axis.YP.rotationDegrees(180));
                stack.mulPose(Axis.XP.rotationDegrees(-90));
            }
        }

        stack.translate(0, originY, originZ);
        float scaleFactor = state.attributes.size.scaleFactor();

        if (part != BodyPart.WINGS) {
            if (state.attributes.isSleeping || state.attributes.isRiptide) {
                stack.translate(0, -0.75F, -0.35F);
                if (state instanceof PlayerPonyRenderState) {
                    stack.translate(0, 0.15F / scaleFactor, 0.4F);
                }
            }

            if (state.attributes.isCrouching) {
                stack.translate(0, -0.13F, 0);
            }

            if (state.attributes.isLyingDown && !state.attributes.isSleeping) {
                stack.translate(0, 0.75F, 0);
            }


            if (state.attributes.isSwimming) {
                stack.translate(0, -0.2F, 0);
            }

            if (state.attributes.isHorsey) {
                stack.translate(0, 0.1F, 0);
            }
        }

        if (state.attributes.isHorsey && part == BodyPart.BODY) {
            stack.scale(1.5F, 1, 1.5F);
        }

        PonyTransformation.forSize(state.attributes.size).transform(state.attributes, part, stack);

        stack.translate(0, -originY, -originZ);
    }

    @Override
    public void transform(T state, BodyPart bodyPart, ModelPart part) {
        PonyTransformation.forSize(state.attributes.size).transform(state.attributes, bodyPart, part);
    }
}
