package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.render.entity.PonyPiglinRenderer;
import com.minelittlepony.util.Sigma;

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

        float progress = state.age * 0.1F + state.limbSwingAnimationProgress * 0.5F;
        float range = 0.08F + state.limbSwingAmplitude * 0.4F;
        rightFlap.roll = -0.5235988F - MathHelper.cos(progress * 1.2F) * range;
        leftFlap.roll =   0.5235988F + MathHelper.cos(progress) * range;
    }

    @Override
    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        super.setHeadRotation(animationProgress, yaw, pitch);
        leftFlap.roll = -(MathHelper.cos(animationProgress * MathHelper.PI * 0.2F * 1.2F) + 2.5F) * -0.2F;
        rightFlap.roll = (MathHelper.cos(animationProgress * MathHelper.PI * 0.2F) + 2.5F) * -0.2F;
    }

    @Override
    protected void rotateLegs(PonyPiglinRenderer.State state) {
        super.rotateLegs(state);

        if (state.activity == PiglinActivity.ADMIRING_ITEM) {
            ModelPart mainArm = getArm(state.mainArm.getOpposite());
            @Sigma float sigma = state.mainArm == Arm.RIGHT ? Sigma.LEFT : Sigma.RIGHT;

            mainArm.yaw = 0.5F * sigma;
            mainArm.pitch = -1.9F;
            mainArm.roll = mainArm.getDefaultTransform().roll();
            mainArm.originY += 4;
            mainArm.originZ += 3;
            mainArm.originX += 2;
            head.setAngles(MathHelper.sin(state.age / 12) / 6 + 0.5F, 0, MathHelper.sin(state.age / 10) / 3F);
        } else if (state.activity == PiglinActivity.DANCING) {

            float speed = state.age / 60;

            head.originX = MathHelper.sin(speed * 10);
            head.originY = MathHelper.sin(speed * 40) + 0.4F;
            head.pitch += MathHelper.sin(speed * 40) / 4 + 0.4F;

            float bodyBob = MathHelper.sin(speed * 40) * 0.35F;
            float legBob = MathHelper.sin(speed * 40) * 0.25F;

            neck.originY = bodyBob;
            body.originY = bodyBob;

            leftLeg.pitch += legBob;
            rightLeg.pitch -= legBob;

            leftArm.roll -= legBob/4;
            rightArm.roll += legBob/4;

            rightArm.pitch += legBob - 0.4F;
            leftArm.pitch -= legBob + 0.4F;
        }
    }

    @Override
    protected boolean shouldAnimateAsZombie(PonyPiglinRenderer.State state) {
        return state.zombified;
    }
}
