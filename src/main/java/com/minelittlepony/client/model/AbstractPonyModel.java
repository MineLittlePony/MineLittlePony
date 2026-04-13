package com.minelittlepony.client.model;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.mson.util.RenderList;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.MathUtil.Angles;
import com.minelittlepony.util.Sigma;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import org.joml.Quaternionf;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends PonyRenderState> extends ClientPonyModel<T> {
    public static final float LEG_SNEAKING_PITCH_ADJUSTMENT = 0.4F;
    public static final float BODY_RIDING_PITCH = MathHelper.PI * 3.8F;
    public static final float BODY_SNEAKING_PITCH = 0.4F;
    public static final float FRONT_LEGS_Y = 8;

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
            .add(bodyRenderList = withStage(BodyPart.BODY).add(body).add(body::applyTransform))
            .add(neckRenderList = withStage(BodyPart.NECK).add(neck))
            .add(headRenderList = withStage(BodyPart.HEAD).add(head))
            .add(legsRenderList = withStage(BodyPart.LEGS).add(leftArm, rightArm, leftLeg, rightLeg));
    }

    protected <P extends SubModel<? super T>> P addPart(P part) {
        parts.add(part);
        return part;
    }

    @Override
    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        mainRenderList.accept(matrices, vertices, light, overlay, color);
    }

    @Override
    public final void renderHead(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        headRenderList.accept(matrices, vertices, light, overlay, color);
    }

    protected void setModelVisibilities(T state) {
        head.visible = state.headVisible;
        hat.visible = head.visible && !state.attributes.isHorsey;
        neck.visible = body.visible;
        if (state.attributes.isHorsey) {
            neck.visible = head.visible;
        } else {
            neck.hidden = !head.visible;
        }
        parts.forEach(part -> part.setVisible(body.visible, state));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void setModelAngles(T entity) {
        resetTransforms();
        head.setAngles(entity.pitch * MathHelper.RADIANS_PER_DEGREE, entity.relativeHeadYaw * MathHelper.RADIANS_PER_DEGREE, 0);

        body.yaw = entity.wobbleAmount;
        neck.yaw = entity.wobbleAmount;

        rotateLegs(entity);
        repositionLegs(entity);
        if (canAnimateArms(entity)) {
            rotateArms(entity);
        }

        if (entity.isInPose(EntityPose.CROUCHING)) {
            ponyCrouch(entity);
        } else if (entity.isInPose(EntityPose.SITTING)) {
            ponySit();
        } else {
            adjustBody(entity, 0, Pivot.ZERO);
            if (entity.attributes.isLyingDown) {
                ponySleep();
            }
        }

        if (entity.attributes.isHorsey) {
            head.originY -= 3;
            head.originZ -= 2;
            head.pitch = 0.5F;
        }

        if (entity.attributes.isChibi) {
            head.xScale += 0.5;
            head.zScale += 0.5;
            head.yScale += 0.5;
            float bobScale = entity.attributes.getMainInterpolator().interpolate("head_bob", entity.limbSwingAmplitude, 120) * 0.4F;
            head.roll += MathHelper.sin(entity.age / 2F) * bobScale;
            head.yaw += MathHelper.sin(entity.age / 3F) * bobScale;
            head.pitch += MathHelper.cos(entity.age / 2F) * bobScale * 1.2F;
        }

        parts.forEach(part -> part.setAngles((PonyModel)this, entity));
        mainRenderList.pose(entity);
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
        neck.setOrigin(0, 0, 0);
        head.setOrigin(0, 0, 0);

        leftLeg.originZ = 14;
        leftLeg.originY = 17;
        leftLeg.pitch = -MathUtil.QUARTER_PIE;
        leftLeg.yaw = -MathHelper.PI / 7;

        leftLeg.pitch += body.pitch;

        rightLeg.originZ = 15;
        rightLeg.originY = 17;
        rightLeg.pitch = -MathUtil.QUARTER_PIE;
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
            QuadrupedLegPosing.swim(state, leftArm, rightArm, leftLeg, rightLeg, state.submergedInWater ? (float)state.getAttributes().motionLerp : 1);
        } else {
            QuadrupedLegPosing.walk(state, leftArm, rightArm, leftLeg, rightLeg);
        }
    }

    protected void repositionLegs(T state) {
        float cos = MathHelper.cos(body.yaw) * 5;

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
    }

    protected void rotateArms(T state) {
        ModelPart leftArm = getArm(Arm.LEFT);
        ModelPart rightArm = getArm(Arm.RIGHT);

        if (!state.attributes.isSwimming && !state.attributes.isGoingFast) {
            alignArmForAction(state, leftArm, Arm.RIGHT);
            alignArmForAction(state, rightArm, Arm.LEFT);
        }
        if (!state.attributes.isLyingDown) {
            if (state.handSwingProgress > 0) {
                switch (state.swingAnimationType) {
                    case NONE:
                        break;
                    case STAB:
                        Lancing.method_75393(this, state);
                        break;
                    case WHACK:
                        QuadrupedalArmPosing.punch(state, state.mainArm == Arm.LEFT ? leftArm : rightArm, body, getHead());
                        break;
                    default:
                        break;

                }
            }
            QuadrupedalArmPosing.idle(state, leftArm, rightArm);
        }
    }

    /**
     * Aligns an arm for the appropriate arm pose
     */
    protected void alignArmForAction(T state, ModelPart arm, Arm side) {
        ArmPose pose = state.getArmPoseForArm(side);
        switch (pose) {
            case EMPTY -> arm.yaw = 0;
            case ITEM -> QuadrupedalArmPosing.holdItem(state, arm, pose, state.getArmPoseForArm(side.getOpposite()), side);
            case BLOCK -> QuadrupedalArmPosing.holdShield(state, arm, pose, state.getArmPoseForArm(side.getOpposite()), side);
            case BOW_AND_ARROW -> QuadrupedalArmPosing.aimBow(state, head, arm);
            case CROSSBOW_HOLD -> QuadrupedalArmPosing.aimCrossbow(state, head, arm, false, side);
            case CROSSBOW_CHARGE -> QuadrupedalArmPosing.aimCrossbow(state, head, arm, true, side);
            case THROW_TRIDENT -> QuadrupedalArmPosing.throwTrident(state, arm, side);
            case SPYGLASS -> QuadrupedalArmPosing.spyglass(state, head, arm, side);
            case TOOT_HORN -> QuadrupedalArmPosing.blowHorn(state, head, arm, side);
            case BRUSH -> QuadrupedalArmPosing.brushBlock(state, arm, side);
            case SPEAR -> Lancing.positionArmForSpear(arm, head, side == Arm.RIGHT, state.getItemStackForArm(side), state);
            default -> {}
        }
    }

    protected boolean canAnimateArms(T state) {
        return true;
    }

    protected void adjustBody(T state, float pitch, Pivot origin) {
        adjustBodyComponents(pitch, origin);
        if (!state.attributes.isHorsey) {
            neck.setOrigin(0, origin.y(), origin.z());
            rightLeg.originY = FRONT_LEGS_Y;
            leftLeg.originY = FRONT_LEGS_Y;
        } else {
            neck.setOrigin(0, origin.y() - 1, origin.z() - 2);
            neck.pitch = Angles._30_DEG;
        }
    }

    protected final void adjustBodyComponents(float pitch, Pivot origin) {
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

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        if (part == BodyPart.NECK) {
            return neck;
        }
        return super.getBodyPart(part);
    }

    @Override
    public final void transformHeldItem(T state, Arm arm, MatrixStack matrices) {
        transform(state, BodyPart.LEGS, matrices);
        ModelPart a = getArm(arm);
        Quaternionf rotation = new Quaternionf().rotationZYX(a.roll, a.yaw, a.pitch);
        matrices.multiply(rotation);
        positionheldItem(state, arm, matrices);
        matrices.multiply(rotation.conjugate());
    }

    protected void positionheldItem(T state, Arm arm, MatrixStack matrices) {
        @Sigma float left = arm == Arm.LEFT ? Sigma.LEFT : Sigma.RIGHT;
        ArmPose pose = arm == Arm.LEFT ? state.leftArmPose : state.rightArmPose;

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
    public void transform(T state, BodyPart part, MatrixStack stack) {
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
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
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
