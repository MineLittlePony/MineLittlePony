package com.minelittlepony.client.model.entity;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.level.block.CopperGolemStatueBlock;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.CopperPonyRenderer;
import com.minelittlepony.mson.util.RenderList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class SpikeModel extends ClientPonyModel<CopperPonyRenderer.State> {

    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;

    private final KeyframeAnimation walkingWithoutItemAnimation;
    private final KeyframeAnimation walkingWithItemAnimation;
    private final KeyframeAnimation spinHeadAnimation;
    private final KeyframeAnimation gettingItemAnimation;
    private final KeyframeAnimation gettingNoItemAnimation;
    private final KeyframeAnimation droppingItemAnimation;
    private final KeyframeAnimation droppingNoItemAnimation;

    public SpikeModel(ModelPart tree) {
        super(tree, false);
        tail = body.getChild("tail");
        tail2 = tail.getChild("tail2");
        tail3 = tail2.getChild("tail3");
        this.walkingWithoutItemAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK.bake(tree);
        this.walkingWithItemAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK_ITEM.bake(tree);
        this.spinHeadAnimation = CopperGolemAnimation.COPPER_GOLEM_IDLE.bake(tree);
        this.gettingItemAnimation = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_GET.bake(tree);
        this.gettingNoItemAnimation = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_NOGET.bake(tree);
        this.droppingItemAnimation = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_DROP.bake(tree);
        this.droppingNoItemAnimation = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_NODROP.bake(tree);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        if (part == BodyPart.TAIL) {
            return tail;
        }
        return super.getBodyPart(part);
    }

    @Override
    public RenderList getRenderList(BodyPart part) {
        return RenderList.of();
    }

    @Override
    protected void setModelAngles(CopperPonyRenderer.State state) {
        root.z += 3;
        float baseRotation = state.walkAnimationPos * 0.6662F; // magic number ahoy
        float scale = state.walkAnimationSpeed;

        tail.yRot = Mth.sin(baseRotation) * scale / state.speedValue;
        tail2.yRot = tail.yRot;
        tail3.yRot = tail.yRot;

        if (state.rightHandItemState.isEmpty() && state.leftHandItemState.isEmpty()) {
            walkingWithoutItemAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2, 2.5F);
        } else {
            walkingWithItemAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2, 2.5F);
            this.clampArmRotations();
        }

        spinHeadAnimation.apply(state.spinHeadAnimationState, state.ageInTicks);
        gettingItemAnimation.apply(state.gettingItemAnimationState, state.ageInTicks);
        gettingNoItemAnimation.apply(state.gettingNoItemAnimationState, state.ageInTicks);
        droppingItemAnimation.apply(state.droppingItemAnimationState, state.ageInTicks);
        droppingNoItemAnimation.apply(state.droppingNoItemAnimationState, state.ageInTicks);
    }

    @Override
    public void translateToHand(AvatarRenderState state, HumanoidArm arm, PoseStack matrices) {
        root.translateAndRotate(matrices);
        getArm(arm).translateAndRotate(matrices);
        matrices.translate(0, -0.15F, 0);
        if (((CopperPonyRenderer.State)state).copperGolemState == CopperGolemState.IDLE) {
            matrices.mulPose(Axis.YP.rotationDegrees(arm == HumanoidArm.RIGHT ? -90.0F : 90.0F));
            matrices.translate(0.0F, 0.0F, 0.125F);
        } else {
            matrices.scale(0.55F, 0.55F, 0.55F);
            matrices.translate(-0.125F, 0.3125F, -0.1875F);
        }
    }

    private void clampArmRotations() {
        rightArm.xRot = Math.min(rightArm.xRot, -0.87266463F);
        leftArm.xRot = Math.min(leftArm.xRot, -0.87266463F);
        rightArm.yRot = Math.min(rightArm.yRot, -0.1134464F);
        leftArm.yRot = Math.max(leftArm.yRot, 0.1134464F);
        rightArm.zRot = Math.min(rightArm.zRot, -0.064577185F);
        leftArm.zRot = Math.max(leftArm.zRot, 0.064577185F);
    }

    public static class BlockModel extends Model<CopperGolemStatueRenderState> {
        private final ModelPart leftArm;
        private final ModelPart rightArm;

        private final ModelPart leftLeg;
        private final ModelPart rightLeg;

        private final ModelPart body;

        private final ModelPart tail;

        public BlockModel(ModelPart root) {
            super(root, RenderTypes::entityCutout);
            leftArm = root.getChild("left_arm");
            rightArm = root.getChild("right_arm");
            leftLeg = root.getChild("left_leg");
            rightLeg = root.getChild("right_leg");
            body = root.getChild("body");
            tail = body.getChild("tail");
        }

        @Override
        public void setupAnim(CopperGolemStatueRenderState state) {
            super.setupAnim(state);
            float scale = PonyConfig.getInstance().getGlobalScaleFactor() * 0.9F;

            root.xScale = scale;
            root.yScale = scale;
            root.zScale = scale;
            root.yRot = state.direction.getOpposite().toYRot() * Mth.DEG_TO_RAD;
            root.zRot = Mth.PI;
            root.y = 16 * 1.5F * root.yScale;

            if (state.pose == CopperGolemStatueBlock.Pose.RUNNING) {
                leftArm.xRot = 0.8F;
                rightArm.xRot = -0.8F;
                leftLeg.xRot = 0.8F;
                rightLeg.xRot = -0.8F;
            }
            if (state.pose == CopperGolemStatueBlock.Pose.SITTING) {
                root.y = 16 * 1.2F * scale;
                leftLeg.xRot = -Mth.HALF_PI;
                rightLeg.xRot = -Mth.HALF_PI;
                tail.xRot += Mth.PI * 0.25F;
            }
            if (state.pose == CopperGolemStatueBlock.Pose.STAR) {
                leftArm.zRot = -2.3F;
                rightArm.zRot = 2.3F;
                leftLeg.zRot = -0.6F;
                rightLeg.zRot = 0.6F;
            }
        }
    }
}
