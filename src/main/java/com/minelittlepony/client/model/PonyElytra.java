package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

/**
 * Modified from ModelElytra.
 */
public class PonyElytra<T extends BipedEntityRenderState> extends EntityModel<T> {

    public boolean isSneaking;

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public PonyElytra(ModelPart root) {
        super(root);
        rightWing = root.getChild("right_wing");
        leftWing = root.getChild("left_wing");
    }

    @Override
    public void setAngles(T state) {
        leftWing.pitch = state.leftWingPitch;
        leftWing.yaw = state.leftWingYaw;
        leftWing.roll = state.leftWingRoll;
        rightWing.pitch = leftWing.pitch;
        rightWing.yaw = -leftWing.yaw;
        rightWing.roll = -leftWing.roll;
    }
}
