package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.CopperPonyRenderer;

public class SpikeModel extends ClientPonyModel<CopperPonyRenderer.State> {

    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;

    public SpikeModel(ModelPart tree) {
        super(tree, false);
        tail = body.getChild("tail");
        tail2 = tail.getChild("tail2");
        tail3 = tail2.getChild("tail3");
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

        if (state.spinHeadAnimationState.isRunning() && state.spinHeadAnimationState.getTimeInMilliseconds(state.age) < 300) {
            head.yaw += MathHelper.sin(state.age / 2F) * MathHelper.PI;
            head.pitch *= 0;
        }

        float armSwingTime = 200F;
        float maxArmAngle = MathHelper.PI * 0.3F;
        ModelPart arm = getArm(state.mainArm);
        ModelPart otherArm = getArm(state.mainArm.getOpposite());
        if (state.gettingItemAnimationState.isRunning()) {
            float progress = MathHelper.clamp(state.gettingItemAnimationState.getTimeInMilliseconds(state.age) / armSwingTime, 0, 1);
            arm.pitch -= maxArmAngle * progress;
            body.pitch += 0.2F * progress;
        }
        if (state.gettingNoItemAnimationState.isRunning()) {
            float progress = MathHelper.clamp(state.gettingNoItemAnimationState.getTimeInMilliseconds(state.age) / armSwingTime, 0, 1);
            arm.pitch -= maxArmAngle * progress;
            body.pitch += 0.2F * progress;
        }
        if (state.droppingItemAnimationState.isRunning()) {
            float progress = MathHelper.clamp(state.droppingItemAnimationState.getTimeInMilliseconds(state.age) / armSwingTime, 0, 1);
            arm.pitch -= maxArmAngle * progress;
        }
        if (state.droppingNoItemAnimationState.isRunning()) {
            float progress = MathHelper.clamp(state.droppingNoItemAnimationState.getTimeInMilliseconds(state.age) / armSwingTime, 0, 1);
            arm.pitch -= MathHelper.TAU * progress;
            head.yaw += MathHelper.sin(state.age / 3F) * MathHelper.TAU * 0.1F;
            head.pitch = MathHelper.lerp(progress, head.pitch, 0.2F);
        }
        arm.pitch = MathHelper.clamp(arm.pitch, -maxArmAngle, maxArmAngle);
        arm.yaw -= maxArmAngle * 0.2F;

        if (state.gettingItemAnimationState.isRunning()) {
            otherArm.pitch = arm.pitch;
            otherArm.yaw = -arm.yaw;
        }
    }
}
