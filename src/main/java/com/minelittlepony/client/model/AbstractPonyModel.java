package com.minelittlepony.client.model;

import com.minelittlepony.api.model.*;
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

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends PonyRenderState> extends ClientPonyModel<T> {
    public static final float LEG_SNEAKING_PITCH_ADJUSTMENT = 0.4F;
    public static final float BODY_RIDING_PITCH = Mth.PI * 3.8F;
    public static final float BODY_SNEAKING_PITCH = 0.4F;
    public static final float FRONT_LEGS_Y = 8;

    public static final Pivot HEAD_SNEAKING = new Pivot(0, 6, -2);
    public static final Pivot BODY_SNEAKING = new Pivot(0, 7, -4);
    public static final Pivot BODY_RIDING = new Pivot(0, 1, 4);
    public static final Pivot FONT_LEGS_SLEEPING = new Pivot(0, -2, 2);
    public static final Pivot BACK_LEGS_SLEEPING = new Pivot(0, -2, -2);

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

    @Override
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
        repositionLegs(entity);
        if (canAnimateArms(entity)) {
            rotateArms(entity);
        }

        if (!entity.attributes.isGoingFast) {
            if (entity.attributes.isCrouching) {
                ponyCrouch(entity);
            } else if (entity.attributes.isSitting) {
                ponySit();
            } else {
                adjustBody(entity, 0, Pivot.ZERO);
                if (entity.attributes.isLyingDown) {
                    ponySleep();
                }
            }
        }

        if (entity.attributes.isHorsey) {
            head.y -= 3;
            head.z -= 2;
            head.xRot = 0.5F;
        }

        if (entity.attributes.isChibi) {
            head.xScale += 0.5;
            head.zScale += 0.5;
            head.yScale += 0.5;
            float bobScale = entity.getAttributes().getMainInterpolator().interpolate("head_bob", entity.walkAnimationSpeed, 120) * 0.4F;
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

        Pivot FONT_LEGS_SLEEPING = new Pivot(0, -2, 2);
        Pivot BACK_LEGS_SLEEPING = new Pivot(0, -2, -2);

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
        leftLeg.xRot = -MathUtil.QUARTER_PIE;
        leftLeg.yRot = -Mth.PI / 7;

        leftLeg.xRot += body.xRot;

        rightLeg.z = 15;
        rightLeg.y = 17;
        rightLeg.xRot = -MathUtil.QUARTER_PIE;
        rightLeg.zRot =  Mth.PI / 7;

        rightLeg.xRot += body.xRot;

        leftArm.zRot = -Mth.PI * 0.06f;
        leftArm.xRot += body.xRot;
        rightArm.zRot = Mth.PI * 0.06f;
        rightArm.xRot += body.xRot;
    }

    /**
    * Used to set the legs rotation based on walking/crouching animations.
    */
    protected void rotateLegs(T state) {
        if (state.attributes.isSwimming) {
            QuadrupedLegPosing.swim(state, leftArm, rightArm, leftLeg, rightLeg, state.submergedInWater ? (float)state.getAttributes().motionLerp : 1);
        } else {
            QuadrupedLegPosing.walk(state, leftArm, rightArm, leftLeg, rightLeg);
        }
    }

    protected void repositionLegs(T state) {
        float cos = Mth.cos(body.yRot) * 5;

        float legRPX = state.attributes.getMainInterpolator().interpolate("legOffset", cos - state.legOutset - 0.001F, 2);
        if (state.attributes.isHorsey) {
            legRPX += 2;
        }

        if (state.attributes.isGoingFast) {
            rightLeg.y -= 4;
            rightLeg.z += 2;
            leftLeg.y -= 4;
            leftLeg.z += 2;
            rightArm.y -= 4;
            rightArm.z -= 2;
            leftArm.y -= 4;
            leftArm.z -= 2;
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

    protected void rotateArms(T state) {
        ModelPart leftArm = getForeLeg(HumanoidArm.LEFT);
        ModelPart rightArm = getForeLeg(HumanoidArm.RIGHT);

        if (!state.attributes.isSwimming && !state.attributes.isGoingFast) {
            alignArmForAction(state, rightArm, HumanoidArm.RIGHT);
            alignArmForAction(state, leftArm, HumanoidArm.LEFT);
        }
        if (!state.attributes.isLyingDown) {
            if (state.attackTime > 0) {
                switch (state.swingAnimationType) {
                    case NONE:
                        break;
                    case STAB:
                        SpearAnimations.thirdPersonAttackHand(this, state);
                        break;
                    case WHACK:
                        QuadrupedalArmPosing.punch(state, state.mainArm == HumanoidArm.LEFT ? leftArm : rightArm, body, getHead());
                        break;
                    default:
                        @Nullable
                        QuadrupedalArmPosing<T, AbstractPonyModel<T>> poser = Untyped.cast(QuadrupedalArmPosing.CUSTOM_SWING_ANIMATIONS.get(state.swingAnimationType));
                        if (poser != null) {
                            poser.alignArmForSwing(state, this);
                        }
                        break;

                }

            }
            QuadrupedalArmPosing.idle(state, leftArm, rightArm);
        }
    }

    /**
     * Aligns an arm for the appropriate arm pose
     */
    protected void alignArmForAction(T state, ModelPart arm, HumanoidArm side) {
        ArmPose pose = state.getArmPoseForArm(side);
        switch (pose) {
            case EMPTY -> arm.yRot = 0;
            case ITEM -> QuadrupedalArmPosing.holdItem(state, arm, pose, state.getArmPoseForArm(side.getOpposite()), side);
            case BLOCK -> QuadrupedalArmPosing.holdShield(state, arm, pose, state.getArmPoseForArm(side.getOpposite()), side);
            case BOW_AND_ARROW -> QuadrupedalArmPosing.aimBow(state, head, arm);
            case CROSSBOW_HOLD -> QuadrupedalArmPosing.aimCrossbow(state, head, arm, false, side);
            case CROSSBOW_CHARGE -> QuadrupedalArmPosing.aimCrossbow(state, head, arm, true, side);
            case THROW_TRIDENT -> QuadrupedalArmPosing.throwTrident(state, arm, side);
            case SPYGLASS -> QuadrupedalArmPosing.spyglass(state, head, arm, side);
            case TOOT_HORN -> QuadrupedalArmPosing.blowHorn(state, head, arm, side);
            case BRUSH -> QuadrupedalArmPosing.brushBlock(state, arm, side);
            case SPEAR -> QuadrupedalArmPosing.holdSpear(state, arm, head, side);
            default -> {
                @Nullable
                QuadrupedalArmPosing<T, AbstractPonyModel<T>> poser = Untyped.cast(QuadrupedalArmPosing.CUSTOM_ARM_POSES.get(pose));
                if (poser != null) {
                    poser.alignArmForAction(state, arm, side);
                }
            }
        }
    }

    protected boolean canAnimateArms(T state) {
        return true;
    }

    protected void adjustBody(T state, float pitch, Pivot origin) {
        adjustBodyComponents(pitch, origin);
        if (!state.attributes.isHorsey) {
            neck.setPos(0, origin.y(), origin.z());
        } else {
            neck.setPos(0, origin.y() - 1, origin.z() - 2);
            neck.xRot = Angles._30_DEG;
        }
    }

    protected final void adjustBodyComponents(float pitch, Pivot origin) {
        body.xRot = pitch;
        body.y = origin.y();
        body.z = origin.z();
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        if (part == BodyPart.NECK) {
            return neck;
        }
        return super.getBodyPart(part);
    }

    @Override
    public final void transformHeldItem(T state, HumanoidArm arm, PoseStack matrices) {
        transform(state, BodyPart.LEGS, matrices);
        ModelPart a = getForeLeg(arm);
        Quaternionf rotation = new Quaternionf().rotationZYX(a.zRot, a.yRot, a.xRot);
        matrices.mulPose(rotation);
        positionheldItem(state, arm, matrices);
        matrices.mulPose(rotation.conjugate());
    }

    protected void positionheldItem(T state, HumanoidArm arm, PoseStack matrices) {
        ArmPose pose = state.getArmPoseForArm(arm);

        if (pose == ArmPose.SPYGLASS) {
            matrices.translate(0, 0.3, 0.3);
            return;
        }

        matrices.translate(-QuadrupedalArmPosing.sigmaOf(arm) * 0.06F, 0.17F, -0.06F);

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
