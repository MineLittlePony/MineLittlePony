package com.minelittlepony.client.model.entity;

import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.CopperGolemAnimations;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CopperGolemState;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.CopperPonyRenderer;

public class SpikeModel extends ClientPonyModel<CopperPonyRenderer.State> {

    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;

    private final Animation walkingWithoutItemAnimation;
    private final Animation walkingWithItemAnimation;
    private final Animation spinHeadAnimation;
    private final Animation gettingItemAnimation;
    private final Animation gettingNoItemAnimation;
    private final Animation droppingItemAnimation;
    private final Animation droppingNoItemAnimation;

    public SpikeModel(ModelPart tree) {
        super(tree, false);
        tail = body.getChild("tail");
        tail2 = tail.getChild("tail2");
        tail3 = tail2.getChild("tail3");
        this.walkingWithoutItemAnimation = CopperGolemAnimations.WALKING_WITHOUT_ITEM.createAnimation(tree);
        this.walkingWithItemAnimation = CopperGolemAnimations.WALKING_WITH_ITEM.createAnimation(tree);
        this.spinHeadAnimation = CopperGolemAnimations.SPIN_HEAD.createAnimation(tree);
        this.gettingItemAnimation = CopperGolemAnimations.GETTING_ITEM.createAnimation(tree);
        this.gettingNoItemAnimation = CopperGolemAnimations.GETTING_NO_ITEM.createAnimation(tree);
        this.droppingItemAnimation = CopperGolemAnimations.DROPPING_ITEM.createAnimation(tree);
        this.droppingNoItemAnimation = CopperGolemAnimations.DROPPING_NO_ITEM.createAnimation(tree);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        if (part == BodyPart.TAIL) {
            return tail;
        }
        return super.getBodyPart(part);
    }

    @Override
    protected void setModelAngles(CopperPonyRenderer.State state) {
        getRootPart().originZ += 3;
        float baseRotation = state.limbSwingAnimationProgress * 0.6662F; // magic number ahoy
        float scale = state.limbSwingAmplitude;

        tail.yaw = MathHelper.sin(baseRotation) * scale / state.limbAmplitudeInverse;
        tail2.yaw = tail.yaw;
        tail3.yaw = tail.yaw;

        if (state.rightHandItemState.isEmpty() && state.leftHandItemState.isEmpty()) {
            walkingWithoutItemAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2, 2.5F);
        } else {
            walkingWithItemAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 2, 2.5F);
            this.clampArmRotations();
        }

        spinHeadAnimation.apply(state.spinHeadAnimationState, state.age);
        gettingItemAnimation.apply(state.gettingItemAnimationState, state.age);
        gettingNoItemAnimation.apply(state.gettingNoItemAnimationState, state.age);
        droppingItemAnimation.apply(state.droppingItemAnimationState, state.age);
        droppingNoItemAnimation.apply(state.droppingNoItemAnimationState, state.age);
    }

    @Override
    public void setArmAngle(PlayerEntityRenderState state, Arm arm, MatrixStack matrices) {
        root.applyTransform(matrices);
        getArm(arm).applyTransform(matrices);
        matrices.translate(0, -0.15F, 0);
        if (((CopperPonyRenderer.State)state).copperGolemState == CopperGolemState.IDLE) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm == Arm.RIGHT ? -90.0F : 90.0F));
            matrices.translate(0.0F, 0.0F, 0.125F);
        } else {
            matrices.scale(0.55F, 0.55F, 0.55F);
            matrices.translate(-0.125F, 0.3125F, -0.1875F);
        }
    }

    private void clampArmRotations() {
        this.rightArm.pitch = Math.min(this.rightArm.pitch, -0.87266463F);
        this.leftArm.pitch = Math.min(this.leftArm.pitch, -0.87266463F);
        this.rightArm.yaw = Math.min(this.rightArm.yaw, -0.1134464F);
        this.leftArm.yaw = Math.max(this.leftArm.yaw, 0.1134464F);
        this.rightArm.roll = Math.min(this.rightArm.roll, -0.064577185F);
        this.leftArm.roll = Math.max(this.leftArm.roll, 0.064577185F);
    }

    public static class BlockModel extends Model<CopperGolemStatueBlockEntityRenderState> {
        private final ModelPart leftArm;
        private final ModelPart rightArm;

        private final ModelPart leftLeg;
        private final ModelPart rightLeg;

        private final ModelPart body;

        private final ModelPart tail;

        public BlockModel(ModelPart root) {
            super(root, RenderLayer::getEntityCutout);
            leftArm = root.getChild("left_arm");
            rightArm = root.getChild("right_arm");
            leftLeg = root.getChild("left_leg");
            rightLeg = root.getChild("right_leg");
            body = root.getChild("body");
            tail = body.getChild("tail");
        }

        @Override
        public void setAngles(CopperGolemStatueBlockEntityRenderState state) {
            super.setAngles(state);
            float scale = PonyConfig.getInstance().getGlobalScaleFactor() * 0.9F;

            getRootPart().xScale = scale;
            getRootPart().yScale = scale;
            getRootPart().zScale = scale;
            getRootPart().yaw = state.facing.getOpposite().getPositiveHorizontalDegrees() * MathHelper.RADIANS_PER_DEGREE;
            getRootPart().roll = MathHelper.PI;
            getRootPart().originY = 16 * 1.5F * getRootPart().yScale;

            if (state.pose == CopperGolemStatueBlock.Pose.RUNNING) {
                leftArm.pitch = 0.8F;
                rightArm.pitch = -0.8F;
                leftLeg.pitch = 0.8F;
                rightLeg.pitch = -0.8F;
            }
            if (state.pose == CopperGolemStatueBlock.Pose.SITTING) {
                getRootPart().originY = 16 * 1.2F * scale;
                leftLeg.pitch = MathHelper.PI * -0.5F;
                rightLeg.pitch = MathHelper.PI * -0.5F;
                tail.pitch += MathHelper.PI * 0.25F;
            }
            if (state.pose == CopperGolemStatueBlock.Pose.STAR) {
                leftArm.roll = -2.3F;
                rightArm.roll = 2.3F;
                leftLeg.roll = -0.6F;
                rightLeg.roll = 0.6F;
            }
        }
    }
}
