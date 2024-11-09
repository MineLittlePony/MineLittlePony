package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.entity.PonyPiglinRenderer;

public class PiglinPonyModel extends ZomponyModel<HostileEntity> {

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
    public void setModelAngles(HostileEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        float progress = ticks * 0.1F + move * 0.5F;
        float range = 0.08F + swing * 0.4F;
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
    protected void rotateLegs(float move, float swing, float ticks, HostileEntity entity) {
        super.rotateLegs(move, swing, ticks, entity);

        if (activity == PiglinActivity.ADMIRING_ITEM) {
            leftArm.yaw = 0.5F;
            leftArm.pitch = -1.9F;
            leftArm.pivotY += 4;
            leftArm.pivotZ += 3;
            leftArm.pivotX += 2;
            head.pitch = MathHelper.sin(ticks / 12) / 6 + 0.5F;
            head.yaw = 0;

            head.roll = MathHelper.sin(ticks / 10) / 3F;
        } else if (activity == PiglinActivity.DANCING) {

            float speed = ticks / 60;

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
    protected boolean isZombified(HostileEntity entity) {
        return !(entity instanceof AbstractPiglinEntity);
    }
}
