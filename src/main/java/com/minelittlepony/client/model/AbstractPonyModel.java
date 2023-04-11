package com.minelittlepony.client.model;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.client.util.render.RenderList;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.MathUtil.Angles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends LivingEntity> extends ClientPonyModel<T> {
    public static final float NECK_X = 0.166F;
    public static final float LEG_SNEAKING_PITCH_ADJUSTMENT = 0.4F;
    public static final float BODY_RIDING_PITCH = MathHelper.PI * 3.8F;
    public static final float BODY_SNEAKING_PITCH = 0.4F;
    public static final float FRONT_LEGS_Y = 8;

    public static final Pivot ORIGIN = new Pivot(0, 0, 0);
    public static final Pivot HEAD_SNEAKING = new Pivot(0, 6, -2);
    public static final Pivot HEAD_SLEEPING = new Pivot(1, 2, 0);
    public static final Pivot BODY_SNEAKING = new Pivot(0, 7, -4);
    public static final Pivot BODY_RIDING = new Pivot(0, 1, 4);
    public static final Pivot FONT_LEGS_SLEEPING = new Pivot(0, 2, 6);
    public static final Pivot BACK_LEGS_SLEEPING = new Pivot(0, 2, -6);

    protected final ModelPart neck;

    public final RenderList helmetRenderList;
    protected final RenderList neckRenderList;
    public final RenderList headRenderList;
    protected final RenderList bodyRenderList;
    protected final RenderList vestRenderList;

    protected final RenderList legsRenderList;
    protected final RenderList sleevesRenderList;

    protected final RenderList mainRenderList;

    private final List<IPart> parts = new ArrayList<>();

    public AbstractPonyModel(ModelPart tree) {
        super(tree);

        neck = tree.getChild("neck");
        mainRenderList = RenderList.of()
            .add(withStage(BodyPart.BODY, bodyRenderList = RenderList.of(body).add(body::rotate)))
            .add(withStage(BodyPart.NECK, neckRenderList = RenderList.of(neck)))
            .add(withStage(BodyPart.HEAD, headRenderList = RenderList.of(head)))
            .add(withStage(BodyPart.LEGS, legsRenderList = RenderList.of().add(leftArm, rightArm, leftLeg, rightLeg)))
            .add(withStage(BodyPart.LEGS, sleevesRenderList = RenderList.of().add(leftSleeve, rightSleeve, leftPants, rightPants)))
            .add(withStage(BodyPart.BODY, vestRenderList = RenderList.of(jacket)))
            .add(withStage(BodyPart.HEAD, helmetRenderList = RenderList.of(hat)));
    }

    protected <P extends IPart> P addPart(P part) {
        parts.add(part);
        return part;
    }

    protected RenderList forPart(Supplier<IPart> part) {
        return (stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> {
            part.get().renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes);
        };
    }

    protected RenderList forPart(IPart part) {
        return (stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> {
            part.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, attributes);
        };
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        mainRenderList.accept(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    protected RenderList withStage(BodyPart part, RenderList action) {
        return (stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> {
            stack.push();
            transform(part, stack);
            action.accept(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
            stack.pop();
        };
    }

    /**
     * Sets the model's various rotation angles.
     */
    @Override
    public final void setAngles(T entity, float limbAngle, float limbSpeed, float animationProgress, float headYaw, float headPitch) {
        attributes.checkRainboom(entity, canFly(), animationProgress);
        PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(entity, this, ModelAttributes.Mode.OTHER);
        super.setAngles(entity, limbAngle, limbSpeed, animationProgress, headYaw, headPitch);

        resetPivot(head, neck, leftArm, rightArm, leftLeg, rightLeg);

        setModelAngles(entity, limbAngle, limbSpeed, animationProgress, headYaw, headPitch);

        leftSleeve.copyTransform(leftArm);
        rightSleeve.copyTransform(rightArm);
        leftPants.copyTransform(leftLeg);
        rightPants.copyTransform(rightLeg);
        jacket.copyTransform(body);
        hat.copyTransform(head);
    }

    protected void setModelAngles(T entity, float limbAngle, float limbSpeed, float animationProgress, float headYaw, float headPitch) {

        float pitch = (float)Math.toRadians(attributes.motionPitch);
        head.setAngles(
                MathHelper.clamp(attributes.isSleeping ? 0.1f : headPitch / 57.29578F, -1.25f - pitch, 0.5f - pitch),
                attributes.isSleeping ? (Math.abs(entity.getUuid().getMostSignificantBits()) % 2.8F) - 1.9F : headYaw / 57.29578F,
                0
        );

        float wobbleAmount = getWobbleAmount();
        body.yaw = wobbleAmount;
        neck.yaw = wobbleAmount;

        rotateLegs(limbAngle, limbSpeed, animationProgress, entity);

        if (onSetModelAngles != null) {
            onSetModelAngles.poseModel(this, limbAngle, limbSpeed, animationProgress, entity);
        }

        if (!attributes.isSwimming && !attributes.isGoingFast) {
            holdItem(limbSpeed);
        }
        swingItem(entity);

        if (attributes.isCrouching) {
            ponyCrouch();
        } else if (riding) {
            ponySit();
        } else {
            adjustBody(0, ORIGIN);

            if (!attributes.isSleeping) {
                animateBreathing(animationProgress);
            }

            if (attributes.isSwimmingRotated) {
                rightLeg.pivotZ -= 1.5F;
                leftLeg.pivotZ -= 1.5F;
            }
        }

        if (attributes.isSleeping) {
            ponySleep();
        }

        if (attributes.isHorsey) {
            head.pivotY -= 3;
            head.pivotZ -= 2;
            head.pitch = 0.5F;
        }

        parts.forEach(part -> part.setPartAngles(attributes, limbAngle, limbSpeed, wobbleAmount, animationProgress));
    }

    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        head.yaw = yaw * MathHelper.RADIANS_PER_DEGREE;
        head.pitch = pitch * MathHelper.RADIANS_PER_DEGREE;
        hat.copyTransform(head);
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void ponyCrouch() {
        adjustBody(BODY_SNEAKING_PITCH, BODY_SNEAKING);
        HEAD_SNEAKING.set(head);

        rightArm.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
        leftArm.pitch -= LEG_SNEAKING_PITCH_ADJUSTMENT;
    }

    protected void ponySleep() {
        rightArm.pitch = -MathUtil.Angles._90_DEG;
        leftArm.pitch = -MathUtil.Angles._90_DEG;

        rightLeg.pitch = MathUtil.Angles._90_DEG;
        leftLeg.pitch = MathUtil.Angles._90_DEG;

        HEAD_SLEEPING.set(head);
        head.pivotZ = sneaking ? -1 : 1;

        FONT_LEGS_SLEEPING.add(rightArm);
        FONT_LEGS_SLEEPING.add(leftArm);
        BACK_LEGS_SLEEPING.add(rightLeg);
        BACK_LEGS_SLEEPING.add(leftLeg);
    }

    protected void ponySit() {
        adjustBodyComponents(BODY_RIDING_PITCH * (attributes.isRidingInteractive ? 2 : 1), BODY_RIDING);
        if (attributes.isRidingInteractive) {
            neck.setPivot(NECK_X, 0, -4);
            head.setPivot(0, -2, -5);
        } else {
            neck.setPivot(NECK_X, 0, 0);
            head.setPivot(0, 0, 0);
        }

        leftLeg.pivotZ = 14;
        leftLeg.pivotY = 17;
        leftLeg.pitch = -MathHelper.PI / 4;
        leftLeg.yaw = -MathHelper.PI / 7;

        leftLeg.pitch += body.pitch;

        rightLeg.pivotZ = 15;
        rightLeg.pivotY = 17;
        rightLeg.pitch = -MathHelper.PI / 4;
        rightLeg.yaw =  MathHelper.PI / 7;

        rightLeg.pitch += body.pitch;

        leftArm.roll = -MathHelper.PI * 0.06f;
        leftArm.pitch += body.pitch;
        rightArm.roll = MathHelper.PI * 0.06f;
        rightArm.pitch += body.pitch;

        if (attributes.isRidingInteractive) {
            leftLeg.yaw = MathHelper.PI / 15;
            leftLeg.pitch = MathHelper.PI / 9;

            leftLeg.pivotZ = 10;
            leftLeg.pivotY = 7;

            rightLeg.yaw = -MathHelper.PI / 15;
            rightLeg.pitch = MathHelper.PI / 9;

            rightLeg.pivotZ = 10;
            rightLeg.pivotY = 7;

            leftArm.pitch = MathHelper.PI / 6;
            rightArm.pitch = MathHelper.PI / 6;

            leftArm.roll *= 2;
            rightArm.roll *= 2;
        }
    }

    /**
    *
    * Used to set the legs rotation based on walking/crouching animations.
    *
    * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
    *
    */
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        if (attributes.isSwimming) {
            rotateLegsSwimming(move, swing, ticks, entity);
        } else {
            rotateLegsOnGround(move, swing, ticks, entity);
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        rightArm.pivotZ = 2 + sin;
        leftArm.pivotZ = 2 - sin;

        float legRPX = attributes.getMainInterpolator().interpolate("legOffset", cos - getLegOutset() - 0.001F, 2);
        if (attributes.isHorsey) {
            legRPX += 2;
        }

        rightArm.pivotX = -legRPX;
        rightLeg.pivotX = -legRPX;

        leftArm.pivotX = legRPX;
        leftLeg.pivotX = legRPX;

        rightArm.yaw += body.yaw;
        leftArm.yaw += body.yaw;

        if (attributes.isHorsey) {
            rightArm.pivotZ = leftArm.pivotZ = -1;
            rightArm.pivotY = leftArm.pivotY = 6;
            rightLeg.pivotZ = leftLeg.pivotZ = 19;
            rightLeg.pivotY = leftLeg.pivotY = 6;
        }
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     */
    protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {

        float lerp = entity.isSwimming() ? (float)attributes.motionLerp : 1;

        float legLeft = (MathUtil.Angles._90_DEG + MathHelper.sin((move / 3) + 2 * MathHelper.PI/3) / 2) * lerp;

        float left = (MathUtil.Angles._90_DEG + MathHelper.sin((move / 3) + 2 * MathHelper.PI) / 2) * lerp;
        float right = (MathUtil.Angles._90_DEG + MathHelper.sin(move / 3) / 2) * lerp;

        leftArm.setAngles(-left, -left/2, left/2);
        rightArm.setAngles(-right, right/2, -right/2);
        leftLeg.setAngles(legLeft, 0, leftLeg.roll);
        rightLeg.setAngles(right, 0, rightLeg.roll);
    }

    /**
     * Rotates legs in quopy fashion for walking.
     *
     */
    protected void rotateLegsOnGround(float move, float swing, float ticks, T entity) {
        float angle = MathHelper.PI * (float) Math.pow(swing, 16);

        float baseRotation = move * 0.6662F; // magic number ahoy
        float scale = swing / 4;

        float rainboomLegLotation = attributes.getMainInterpolator().interpolate(
                "rainboom_leg_rotation",
                attributes.isGoingFast ? 1 : 0,
                5
        );
        float yAngle = 0.2F * rainboomLegLotation;

        leftArm.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, 0);
        rightArm.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI + angle / 2) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, 0);
        leftLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI - (angle * 0.4f)) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, leftLeg.roll);
        rightLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle / 5) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, rightLeg.roll);
    }

    protected float getLegOutset() {
        if (attributes.isSleeping) {
            return 3.6f;
        }

        if (attributes.isCrouching) {
            return 1;
        }

        return 5;
    }

    /**
     * Adjusts legs as if holding an item. Delegates to the correct arm/leg/limb as necessary.
     */
    protected void holdItem(float limbSpeed) {
        alignArmForAction(getArm(Arm.LEFT), leftArmPose, rightArmPose, limbSpeed, 1);
        alignArmForAction(getArm(Arm.RIGHT), rightArmPose, leftArmPose, limbSpeed, -1);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        switch (part) {
            default:
            case HEAD: return head;
            case NECK: return neck;
            case TAIL:
            case LEGS:
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
    protected void alignArmForAction(ModelPart arm, ArmPose pose, ArmPose complement, float limbSpeed, float sigma) {
        switch (pose) {
            case ITEM:
                arm.yaw = 0;

                boolean both = pose == complement;

                if (attributes.shouldLiftArm(pose, complement, sigma)) {
                    float swag = 1;
                    if (!isFlying() && both) {
                        swag -= (float)Math.pow(limbSpeed, 2);
                    }

                    float mult = 1 - swag/2;
                    arm.pitch = arm.pitch * mult - (MathHelper.PI / 10) * swag;
                    arm.roll = -sigma * (MathHelper.PI / 15);

                    if (attributes.isCrouching) {
                        arm.pivotX -= sigma * 2;
                    }
                }

                break;
            case EMPTY:
                arm.yaw = 0;
                break;
            case BLOCK:
                arm.pitch = (arm.pitch / 2 - 0.9424779F) - 0.3F;
                arm.yaw = sigma * MathHelper.PI / 9;
                if (complement == pose) {
                    arm.yaw -= sigma * MathHelper.PI / 18;
                }
                arm.pivotX += sigma;
                arm.pivotZ += 3;
                if (attributes.isCrouching) {
                    arm.pivotY += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(arm, limbSpeed);
                break;
            case CROSSBOW_HOLD:
                aimBow(arm, limbSpeed);

                arm.pitch = head.pitch - MathUtil.Angles._90_DEG;
                arm.yaw = head.yaw + 0.06F;
                break;
            case CROSSBOW_CHARGE:
                aimBow(arm, limbSpeed);

                arm.pitch = -0.8F;
                arm.yaw = head.yaw + 0.06F;
                break;
            case THROW_SPEAR:
                arm.pitch = MathUtil.Angles._90_DEG * 2;
                break;
            case SPYGLASS:
                float addedPitch = sneaking ? -0.2617994F : 0;
                float minPitch = sneaking ? -1.8F : -2.4F;
                arm.pitch = MathHelper.clamp(head.pitch - 1.9198622F - addedPitch, minPitch, 3.3F);
                arm.yaw = head.yaw;

                if (sneaking) {
                    arm.pivotY += 9;
                    arm.pivotX -= 6 * sigma;
                    arm.pivotZ -= 2;
                }
                if (getSize() == Sizes.TALL) {
                    arm.pivotY += 1;
                }
                if (getSize() == Sizes.FOAL) {
                    arm.pivotY -= 2;
                }

                break;
            default:
                break;
        }
    }

    protected void aimBow(ModelPart arm, float limbSpeed) {
        arm.pitch = MathUtil.Angles._270_DEG + head.pitch + (MathHelper.sin(limbSpeed * 0.067F) * 0.05F);
        arm.yaw = head.yaw - 0.06F;
        arm.roll = MathHelper.cos(limbSpeed * 0.09F) * 0.05F + 0.05F;

        if (sneaking) {
            arm.pivotY += 4;
        }
    }

    /**
     * Animates arm swinging. Delegates to the correct arm/leg/limb as neccessary.
     *
     * @param entity     The entity we are being called for.
     */
    protected void swingItem(T entity) {
        if (getSwingAmount() > 0 && !attributes.isSleeping) {
            Arm mainSide = getPreferredArm(entity);

            swingArm(getArm(mainSide));
        }
    }

    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    protected void swingArm(ModelPart arm) {
        float swing = 1 - (float)Math.pow(1 - getSwingAmount(), 3);

        float deltaX = MathHelper.sin(swing * MathHelper.PI);
        float deltaZ = MathHelper.sin(getSwingAmount() * MathHelper.PI);

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
    protected void animateBreathing(float animationProgress) {
        float cos = MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(animationProgress * 0.067F) * 0.05F;

        if (attributes.shouldLiftArm(rightArmPose, leftArmPose, -1)) {
            ModelPart arm = getArm(Arm.RIGHT);
            arm.roll += cos;
            arm.pitch += sin;
        }

        if (attributes.shouldLiftArm(leftArmPose, rightArmPose, 1)) {
            ModelPart arm = getArm(Arm.LEFT);
            arm.roll += cos;
            arm.pitch += sin;
        }
    }

    protected void adjustBody(float pitch, Pivot pivot) {
        adjustBodyComponents(pitch, pivot);
        if (!attributes.isHorsey) {
            neck.setPivot(NECK_X + pitch, pivot.y(), pivot.z());
            rightLeg.pivotY = FRONT_LEGS_Y;
            leftLeg.pivotY = FRONT_LEGS_Y;
        } else {
            neck.setPivot(NECK_X + pitch, pivot.y() - 1, pivot.z() - 2);
            neck.pitch = Angles._30_DEG;
        }
    }

    protected void adjustBodyComponents(float pitch, Pivot pivot) {
        body.pitch = pitch;
        body.pivotY = pivot.y();
        body.pivotZ = pivot.z();
    }

    @Override
    public float getRiderYOffset() {
        switch ((Sizes)getSize()) {
            case NORMAL: return 0.4F;
            case FOAL:
            case TALL:
            case BULKY:
            default: return 0.25F;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        neck.visible = visible;
        hat.visible &= !attributes.isHorsey;
        parts.forEach(part -> part.setVisible(visible, attributes));
    }

    @Override
    public final void setArmAngle(Arm arm, MatrixStack matrices) {
        super.setArmAngle(arm, matrices);
        positionheldItem(arm, matrices);
    }

    protected void positionheldItem(Arm arm, MatrixStack matrices) {
        float left = arm == Arm.LEFT ? -1 : 1;

        UseAction action = getAttributes().heldStack.getUseAction();

        if (action == UseAction.SPYGLASS && getAttributes().itemUseTime > 0) {

            Arm main = getAttributes().mainArm;
            if (getAttributes().activeHand == Hand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                matrices.translate(left * -0.05F, 0.5F, 0.2F);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-60));

                return;
            }
        }

        if (getAttributes().isRidingInteractive) {
            matrices.translate(left / 10, -0.2F, -0.5F);
        }

        matrices.translate(left * 0.1F, 0.45F, 0);
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {

        if (attributes.isHorsey) {
            stack.translate(0, 0.1F, 0);
        }

        if (attributes.isSleeping || attributes.isRiptide) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        }

        if (attributes.isHorsey) {
            if (part == BodyPart.BODY) {
                stack.scale(1.5F, 1, 1.5F);
            }

            neck.visible = head.visible;
        }

        PonyTransformation.forSize(getSize()).transform(this, part, stack);
    }
}
