package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

import com.minelittlepony.client.render.entity.PonyPiglinRenderer;

public class PiglinPonyModel extends ZomponyModel<PonyPiglinRenderer.State> {

    private final ModelPart leftFlap;
    private final ModelPart rightFlap;

    public PiglinPonyModel(ModelPart tree) {
        super(tree);
        leftFlap = tree.getChild("left_flap");
        rightFlap = tree.getChild("right_flap");
    }

    @Override
    public void setModelAngles(PonyPiglinRenderer.State state) {
        super.setModelAngles(state);

        float progress = state.ageInTicks * 0.1F + state.walkAnimationPos * 0.5F;
        float range = 0.08F + state.walkAnimationSpeed * 0.4F;
        rightFlap.zRot = -0.5235988F - Mth.cos(progress * 1.2F) * range;
        leftFlap.zRot =   0.5235988F + Mth.cos(progress) * range;
    }

    @Override
    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        super.setHeadRotation(animationProgress, yaw, pitch);
        leftFlap.zRot = -(float)(-(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F * 1.2F)) + 2.5)) * 0.2F;
        rightFlap.zRot = -(float)(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F)) + 2.5) * 0.2F;
    }

    @Override
    protected void rotateLegs(PonyPiglinRenderer.State state) {
        super.rotateLegs(state);

        if (state.activity == PiglinArmPose.ADMIRING_ITEM) {
            leftArm.yRot = 0.5F;
            leftArm.xRot = -1.9F;
            leftArm.y += 4;
            leftArm.z += 3;
            leftArm.x += 2;
            head.xRot = Mth.sin(state.ageInTicks / 12) / 6 + 0.5F;
            head.yRot = 0;

            head.zRot = Mth.sin(state.ageInTicks / 10) / 3F;
        } else if (state.activity == PiglinArmPose.DANCING) {

            float speed = state.ageInTicks / 60;

            head.x = Mth.sin(speed * 10);
            head.y = Mth.sin(speed * 40) + 0.4F;
            head.xRot += Mth.sin(speed * 40) / 4 + 0.4F;

            float bodyBob = Mth.sin(speed * 40) * 0.35F;
            float legBob = Mth.sin(speed * 40) * 0.25F;

            neck.y = bodyBob;
            body.y = bodyBob;

            leftLeg.xRot += legBob;
            rightLeg.xRot -= legBob;

            leftArm.zRot -= legBob/4;
            rightArm.zRot += legBob/4;

            rightArm.xRot += legBob - 0.4F;
            leftArm.xRot -= legBob + 0.4F;
        }
    }

    @Override
    protected boolean shouldLiftBothArms(PonyPiglinRenderer.State state) {
        return state.zombified && super.shouldLiftBothArms(state);
    }
}
