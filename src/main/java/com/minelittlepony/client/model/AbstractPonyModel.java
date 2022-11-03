package com.minelittlepony.client.model;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.client.model.armour.ArmourWrapper;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.mson.util.PartUtil;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Foundation class for all types of ponies.
 */
public abstract class AbstractPonyModel<T extends LivingEntity> extends ClientPonyModel<T> {

    protected final ModelPart upperTorso;
    protected final ModelPart upperTorsoOverlay;

    protected final ModelPart neck;

    public AbstractPonyModel(ModelPart tree) {
        super(tree);

        upperTorso = tree.getChild("upper_torso");
        upperTorsoOverlay = tree.getChild("saddle");
        neck = tree.getChild("neck");
    }

    @Override
    public IArmour<?> createArmour() {
        return ArmourWrapper.of(PonyArmourModel::new);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * @param move      Entity motion parameter
     *                  i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param ticks     Total whole and partial ticks since the entity's existence.
     *                  Used in animations together with {@code swing} and {@code move}.
     * @param headYaw   Horizontal head motion in radians.
     * @param headPitch Vertical head motion in radians.
     * @param scale     Scaling factor used to render this model.
     *                  Determined by the return value of {@link RenderLivingBase.prepareScale}.
     *                  Usually {@code 0.0625F}.
     * @param entity    The entity we're being called for.
     */
    @Override
    public final void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        attributes.checkRainboom(entity, swing, canFly(), ticks);
        PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(entity, this, ModelAttributes.Mode.OTHER);
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        head.pivotY = head.getDefaultTransform().pivotY;
        head.pivotX = head.getDefaultTransform().pivotX;
        head.pivotZ = head.getDefaultTransform().pivotZ;

        setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        leftSleeve.copyTransform(leftArm);
        rightSleeve.copyTransform(rightArm);
        leftPants.copyTransform(leftLeg);
        rightPants.copyTransform(rightLeg);
        jacket.copyTransform(body);
        hat.copyTransform(head);
        upperTorsoOverlay.copyTransform(upperTorso);
    }

    protected void setModelAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        rotateHead(headYaw, headPitch);
        shakeBody(move, swing, getWobbleAmount(), ticks);
        rotateLegs(move, swing, ticks, entity);

        if (onSetModelAngles != null) {
            onSetModelAngles.poseModel(this, move, swing, ticks, entity);
        }

        if (!attributes.isSwimming && !attributes.isGoingFast) {
            holdItem(swing);
        }
        swingItem(entity);

        if (attributes.isCrouching) {
            ponyCrouch();
        } else if (riding) {
            ponySit();
        } else {
            adjustBody(BODY_ROT_X, BODY_RP_Y, BODY_RP_Z);

            rightLeg.pivotY = FRONT_LEG_RP_Y;
            leftLeg.pivotY = FRONT_LEG_RP_Y;

            if (!attributes.isSleeping) {
                animateBreathing(ticks);
            }

            if (attributes.isSwimmingRotated) {
                head.setPivot(0, HEAD_RP_Y_SWIM, HEAD_RP_Z_SWIM);
            }
        }

        if (attributes.isSleeping) {
            ponySleep();
        }
    }

    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        head.yaw = yaw * ((float)Math.PI / 180);
        head.pitch = pitch * ((float)Math.PI / 180);
        hat.copyTransform(head);
    }

    /**
     * Aligns legs to a sneaky position.
     */
    protected void ponyCrouch() {
        adjustBody(BODY_ROT_X_SNEAK, BODY_RP_Y_SNEAK, BODY_RP_Z_SNEAK);
        head.setPivot(HEAD_RP_X_SNEAK, HEAD_RP_Y_SNEAK, HEAD_RP_Z_SNEAK);

        rightArm.pitch -= LEG_ROT_X_SNEAK_ADJ;
        leftArm.pitch -= LEG_ROT_X_SNEAK_ADJ;

        leftLeg.pivotY = FRONT_LEG_RP_Y_SNEAK;
        rightLeg.pivotY = FRONT_LEG_RP_Y_SNEAK;
    }

    protected void ponySleep() {
        rightArm.pitch = ROTATE_270;
        leftArm.pitch = ROTATE_270;

        rightLeg.pitch = ROTATE_90;
        leftLeg.pitch = ROTATE_90;

        head.setPivot(HEAD_RP_X_SLEEP, HEAD_RP_Y_SLEEP, sneaking ? -1 : 1);

        PartUtil.shift(rightArm, 0, LEG_SLEEP_OFFSET_Y, FRONT_LEG_SLEEP_OFFSET_Z);
        PartUtil.shift(leftArm, 0, LEG_SLEEP_OFFSET_Y, FRONT_LEG_SLEEP_OFFSET_Z);
        PartUtil.shift(rightLeg, 0, LEG_SLEEP_OFFSET_Y, BACK_LEG_SLEEP_OFFSET_Z);
        PartUtil.shift(leftLeg, 0, LEG_SLEEP_OFFSET_Y, BACK_LEG_SLEEP_OFFSET_Z);
    }

    protected void ponySit() {
        if (attributes.isRidingInteractive) {
            adjustBodyComponents(BODY_ROT_X_RIDING * 2, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
            adjustNeck(BODY_ROT_X * 2, BODY_RP_Y, BODY_RP_Z - 4);
            head.setPivot(0, -2, -5);
        } else {
            adjustBodyComponents(BODY_ROT_X_RIDING, BODY_RP_Y_RIDING, BODY_RP_Z_RIDING);
            adjustNeck(BODY_ROT_X, BODY_RP_Y, BODY_RP_Z);
            head.setPivot(0, 0, 0);
        }

        leftLeg.pivotZ = 14;
        leftLeg.pivotY = 9;
        leftLeg.pitch = -PI / 4;
        leftLeg.yaw = -PI / 5;

        rightLeg.pivotZ = 15;
        rightLeg.pivotY = 9;
        rightLeg.pitch = -PI / 4;
        rightLeg.yaw =  PI / 5;

        leftArm.roll = -PI * 0.06f;
        rightArm.roll = PI * 0.06f;

        if (attributes.isRidingInteractive) {
            leftLeg.yaw = PI / 15;
            leftLeg.pitch = PI / 9;

            leftLeg.pivotZ = 10;
            leftLeg.pivotY = 7;

            rightLeg.yaw = -PI / 15;
            rightLeg.pitch = PI / 9;

            rightLeg.pivotZ = 10;
            rightLeg.pivotY = 7;

            leftArm.pitch = PI / 6;
            rightArm.pitch = PI / 6;

            leftArm.roll *= 2;
            rightArm.roll *= 2;
        }
    }

    /**
     * Sets the model's various rotation angles.
     *
     * @param move      Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing     Degree to which each 'limb' swings.
     * @param bodySwing Horizontal (Y) body rotation.
     * @param ticks       Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     */
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        upperTorso.yaw = bodySwing;
        body.yaw = bodySwing;
        neck.yaw = bodySwing;
    }

    /**
     * Called to update the head rotation.
     *
     * @param x     New rotation X
     * @param y     New rotation Y
     */
    private void rotateHead(float headYaw, float headPitch) {
        headYaw = attributes.isSleeping ? (Math.abs(attributes.interpolatorId.getMostSignificantBits()) % 2.8F) - 1.9F : headYaw / 57.29578F;
        headPitch = attributes.isSleeping ? 0.1f : headPitch / 57.29578F;

        if (attributes.isSwimming && attributes.motionPitch != 0) {
            headPitch -= 0.9F;
        }

        float pitch = (float)Math.toRadians(attributes.motionPitch);
        head.setAngles(
                MathHelper.clamp(headPitch, -1.25f - pitch, 0.5f - pitch),
                attributes.isSwimmingRotated ? 0 : headYaw,
                attributes.isSwimmingRotated ? -headYaw : 0
        );
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
        } else if (attributes.isGoingFast) {
            rotateLegsInFlight(move, swing, ticks, entity);
        } else {
            rotateLegsOnGround(move, swing, ticks, entity);
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        rightArm.pivotZ = 2 + sin;
        leftArm.pivotZ = 2 - sin;

        float legRPX = cos - getLegOutset() - 0.001F;

        legRPX = getMetadata().getInterpolator(attributes.interpolatorId).interpolate("legOffset", legRPX, 3);

        rightArm.pivotX = -legRPX;
        rightLeg.pivotX = -legRPX;

        leftArm.pivotX = legRPX;
        leftLeg.pivotX = legRPX;

        rightArm.yaw += body.yaw;
        leftArm.yaw += body.yaw;

        rightArm.pivotY = leftArm.pivotY = 8;
        rightLeg.pivotZ = leftLeg.pivotZ = 11;
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     */
    protected void rotateLegsSwimming(float move, float swing, float ticks, T entity) {

        float lerp = entity.isSwimming() ? (float)attributes.motionLerp : 1;

        float legLeft = (ROTATE_90 + MathHelper.sin((move / 3) + 2 * PI/3) / 2) * lerp;

        float left = (ROTATE_90 + MathHelper.sin((move / 3) + 2 * PI) / 2) * lerp;
        float right = (ROTATE_90 + MathHelper.sin(move / 3) / 2) * lerp;

        leftArm.setAngles(-left, -left/2, left/2);
        rightArm.setAngles(-right, right/2, -right/2);
        leftLeg.setAngles(legLeft, 0, leftLeg.roll);
        rightLeg.setAngles(right, 0, rightLeg.roll);
    }

    /**
     * Rotates legs in quopy fashion whilst flying.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     *
     */
    protected void rotateLegsInFlight(float move, float swing, float ticks, Entity entity) {
        float armX = attributes.isGoingFast ? ROTATE_270 : MathHelper.sin(-swing / 2);
        float legX = attributes.isGoingFast ? ROTATE_90 : MathHelper.sin(swing / 2);

        leftArm.setAngles(armX, -0.2F, 0);
        rightArm.setAngles(armX, 0.2F, 0);
        leftLeg.setAngles(legX, 0.2F, leftLeg.roll);
        rightLeg.setAngles(legX, -0.2F, rightLeg.roll);
    }

    /**
     * Rotates legs in quopy fashion for walking.
     *
     * Takes the same parameters as {@link AbstractPonyModel.setRotationAndAngles}
     *
     */
    protected void rotateLegsOnGround(float move, float swing, float ticks, T entity) {
        float angle = PI * (float) Math.pow(swing, 16);

        float baseRotation = move * 0.6662F; // magic number ahoy
        float scale = swing / 4;

        leftArm.setAngles(MathHelper.cos(baseRotation + angle) * scale, 0, 0);
        rightArm.setAngles(MathHelper.cos(baseRotation + PI + angle / 2) * scale, 0, 0);
        leftLeg.setAngles(MathHelper.cos(baseRotation + PI - (angle * 0.4f)) * scale, 0, leftLeg.roll);
        rightLeg.setAngles(MathHelper.cos(baseRotation + angle / 5) * scale, 0, rightLeg.roll);
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
    protected void holdItem(float swing) {
        alignArmForAction(getArm(Arm.LEFT), leftArmPose, rightArmPose, swing, 1);
        alignArmForAction(getArm(Arm.RIGHT), rightArmPose, leftArmPose, swing, -1);
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

    protected boolean shouldLiftArm(ArmPose pose, ArmPose complement, float sigma) {
        return pose != ArmPose.EMPTY
                && (pose != complement || sigma == (attributes.isLeftHanded ? 1 : -1))
                && (complement != ArmPose.BLOCK && complement != ArmPose.CROSSBOW_HOLD);
    }

    /**
     * Aligns an arm for the appropriate arm pose
     *
     * @param arm   The arm model to align
     * @param pose  The post to align to
     * @param swing     Degree to which each 'limb' swings.
     */
    protected void alignArmForAction(ModelPart arm, ArmPose pose, ArmPose complement, float swing, float sigma) {
        switch (pose) {
            case ITEM:
                arm.yaw = 0;

                boolean both = pose == complement;

                if (shouldLiftArm(pose, complement, sigma)) {
                    float swag = 1;
                    if (!isFlying() && both) {
                        swag -= (float)Math.pow(swing, 2);
                    }

                    float mult = 1 - swag/2;
                    arm.pitch = arm.pitch * mult - (PI / 10) * swag;
                    arm.roll = -sigma * (PI / 15);

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
                arm.yaw = sigma * PI / 9;
                if (complement == pose) {
                    arm.yaw -= sigma * PI / 18;
                }
                arm.pivotX += sigma;
                arm.pivotZ += 3;
                if (attributes.isCrouching) {
                    arm.pivotY += 4;
                }
                break;
            case BOW_AND_ARROW:
                aimBow(arm, swing);
                break;
            case CROSSBOW_HOLD:
                aimBow(arm, swing);

                arm.pitch = head.pitch - ROTATE_90;
                arm.yaw = head.yaw + 0.06F;
                break;
            case CROSSBOW_CHARGE:
                aimBow(arm, swing);

                arm.pitch = -0.8F;
                arm.yaw = head.yaw + 0.06F;
                break;
            case THROW_SPEAR:
                arm.pitch = ROTATE_90 * 2;
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

    protected void aimBow(ModelPart arm, float ticks) {
        arm.pitch = ROTATE_270 + head.pitch + (MathHelper.sin(ticks * 0.067F) * 0.05F);
        arm.yaw = head.yaw - 0.06F;
        arm.roll = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;

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

        float deltaX = MathHelper.sin(swing * PI);
        float deltaZ = MathHelper.sin(getSwingAmount() * PI);

        float deltaAim = deltaZ * (0.7F - head.pitch) * 0.75F;

        arm.pitch -= deltaAim + deltaX * 1.2F;
        arm.yaw += body.yaw * 2;
        arm.roll = -deltaZ * 0.4F;
    }

    /**
     * Animates the arm's breathing animation when holding items.
     *
     * @param ticks       Total whole and partial ticks since the entity's existence.
     *                    Used in animations together with {@code swing} and {@code move}.
     */
    protected void animateBreathing(float ticks) {
        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

        if (shouldLiftArm(rightArmPose, leftArmPose, -1)) {
            ModelPart arm = getArm(Arm.RIGHT);
            arm.roll += cos;
            arm.pitch += sin;
        }

        if (shouldLiftArm(leftArmPose, rightArmPose, 1)) {
            ModelPart arm = getArm(Arm.LEFT);
            arm.roll += cos;
            arm.pitch += sin;
        }
    }

    protected void adjustBody(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);
        adjustNeck(rotateAngleX, rotationPointY, rotationPointZ);
    }

    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        body.pitch = rotateAngleX;
        body.pivotY = rotationPointY;
        body.pivotZ = rotationPointZ;

        upperTorso.pitch = rotateAngleX;
        upperTorso.pivotY = rotationPointY;
        upperTorso.pivotZ = rotationPointZ;
    }

    private void adjustNeck(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        neck.setPivot(NECK_ROT_X + rotateAngleX, rotationPointY, rotationPointZ);
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
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        renderStage(BodyPart.BODY, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderBody);
        renderStage(BodyPart.NECK, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderNeck);
        renderStage(BodyPart.HEAD, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderHead);
        renderStage(BodyPart.LEGS, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderLegs);

        renderStage(BodyPart.LEGS, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderSleeves);
        renderStage(BodyPart.BODY, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderVest);
        renderStage(BodyPart.HEAD, stack, vertices, overlayUv, lightUv, red, green, blue, alpha, this::renderHelmet);
    }

    protected void renderStage(BodyPart part, MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, RenderStage action) {
        stack.push();
        transform(part, stack);
        action.accept(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        stack.pop();
    }

    public void renderHead(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        head.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    public void renderHelmet(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        hat.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    protected void renderNeck(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        neck.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        body.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        upperTorso.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        body.rotate(stack);
    }

    protected void renderVest(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        jacket.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        upperTorsoOverlay.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    protected void renderLegs(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        if (!sneaking) {
            body.rotate(stack);
        }

        leftArm.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightArm.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        leftLeg.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightLeg.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    protected void renderSleeves(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        if (!sneaking) {
            body.rotate(stack);
        }

        leftSleeve.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightSleeve.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        leftPants.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        rightPants.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        upperTorso.visible = visible;
        upperTorsoOverlay.visible = visible;

        neck.visible = visible;
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {
        if (attributes.isSleeping || attributes.isRiptide) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        }

        if (part == BodyPart.HEAD) {
           stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(attributes.motionPitch));
        }

        PonyTransformation.forSize(getSize()).transform(this, part, stack);
    }

    protected interface RenderStage {
        void accept(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha);
    }
}
