package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

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
    protected ArmPose getArmPose(PlayerEntityRenderState p, Arm arm) {
        if (p instanceof PonyPiglinRenderer.State state) {
            return switch (arm) {
                case LEFT -> switch (state.activity) {
                    case CROSSBOW_HOLD -> ArmPose.CROSSBOW_HOLD;
                    case CROSSBOW_CHARGE -> ArmPose.CROSSBOW_CHARGE;
                    default -> ArmPose.EMPTY;
                };
                case RIGHT -> switch (state.activity) {
                    case ADMIRING_ITEM -> ArmPose.ITEM;
                    default -> ArmPose.EMPTY;
                };
            };
        }

        return super.getArmPose(p, arm);
    }

    @Override
    public void setModelAngles(PonyPiglinRenderer.State state) {
        super.setModelAngles(state);

        float progress = state.age * 0.1F + state.limbFrequency * 0.5F;
        float range = 0.08F + state.limbAmplitudeMultiplier * 0.4F;
        rightFlap.roll = -0.5235988F - MathHelper.cos(progress * 1.2F) * range;
        leftFlap.roll =   0.5235988F + MathHelper.cos(progress) * range;
    }

    @Override
    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        super.setHeadRotation(animationProgress, yaw, pitch);
        leftFlap.roll = -(float)(-(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F * 1.2F)) + 2.5)) * 0.2F;
        rightFlap.roll = -(float)(Math.cos((double)(animationProgress * (float) Math.PI * 0.2F)) + 2.5) * 0.2F;
    }

    @Override
    protected void rotateLegs(PonyPiglinRenderer.State state) {
        super.rotateLegs(state);

        if (state.activity == PiglinActivity.ADMIRING_ITEM) {
            leftArm.yaw = 0.5F;
            leftArm.pitch = -1.9F;
            leftArm.pivotY += 4;
            leftArm.pivotZ += 3;
            leftArm.pivotX += 2;
            head.pitch = MathHelper.sin(state.age / 12) / 6 + 0.5F;
            head.yaw = 0;

            head.roll = MathHelper.sin(state.age / 10) / 3F;
        } else if (state.activity == PiglinActivity.DANCING) {

            float speed = state.age / 60;

            head.pivotX = MathHelper.sin(speed * 10);
            head.pivotY = MathHelper.sin(speed * 40) + 0.4F;
            head.pitch += MathHelper.sin(speed * 40) / 4 + 0.4F;

            float bodyBob = MathHelper.sin(speed * 40) * 0.35F;
            float legBob = MathHelper.sin(speed * 40) * 0.25F;

            neck.pivotY = bodyBob;
            body.pivotY = bodyBob;

            leftLeg.pitch += legBob;
            rightLeg.pitch -= legBob;

            leftArm.roll -= legBob/4;
            rightArm.roll += legBob/4;

            rightArm.pitch += legBob - 0.4F;
            leftArm.pitch -= legBob + 0.4F;
        }
    }

    @Override
    protected boolean shouldLiftBothArms(PonyPiglinRenderer.State state) {
        return state.zombified && super.shouldLiftBothArms(state);
    }
}
