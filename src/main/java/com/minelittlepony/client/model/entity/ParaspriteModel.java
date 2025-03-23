package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.render.entity.VexRenderer;

public class ParaspriteModel extends EntityModel<VexRenderer.State> {
    private final ModelPart body;
    private final ModelPart jaw;
    private final ModelPart lips;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    private final ModelPart leftWing2;
    private final ModelPart rightWing2;

    public ParaspriteModel(ModelPart root) {
        super(root, RenderLayer::getEntityTranslucent);
        body = root.getChild("body");
        jaw = body.getChild("jaw");
        lips = body.getChild("lips");
        leftWing = root.getChild("leftWing");
        rightWing = root.getChild("rightWing");
        leftWing2 = root.getChild("leftWing2");
        rightWing2 = root.getChild("rightWing2");
    }

    @Override
    public void setAngles(VexRenderer.State state) {
        root.pitch = state.bodyPitch;
        body.pitch = 0;
        root.pitch = state.pitch * MathHelper.RADIANS_PER_DEGREE;
        root.yaw = state.relativeHeadYaw * MathHelper.RADIANS_PER_DEGREE;

        jaw.originY = Math.max(0, 1.2F * state.jawOpenAmount);
        lips.originY = jaw.originY - 0.9F;
        lips.visible = state.jawOpenAmount > 0;
        body.pitch += 0.3F * state.jawOpenAmount;
        jaw.pitch = 0.4F * state.jawOpenAmount;
        lips.pitch = 0.2F * state.jawOpenAmount;

        leftWing.pitch = 0;
        leftWing.roll = state.wingRoll;
        leftWing.yaw = state.wingYaw;

        rightWing.pitch = 0;
        rightWing.roll = -state.wingRoll;
        rightWing.yaw = -state.wingYaw;

        leftWing2.pitch = 0;
        leftWing2.roll = state.innerWingRoll;
        leftWing2.yaw = state.innerWingPitch;

        rightWing2.pitch = 0;
        rightWing2.roll = -state.innerWingRoll;
        rightWing2.yaw = -state.innerWingPitch;
    }
}
