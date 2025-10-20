package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.entity.PonyStandRenderer;

public class PonyArmorStandEntityArmorModel extends PonyArmourModel<PonyStandRenderer.PonyState> {
    public PonyArmorStandEntityArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setModelAngles(PonyStandRenderer.PonyState state) {
        super.setModelAngles(state);
        head.pitch = state.angles.headRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        head.yaw = state.angles.headRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        head.roll = state.angles.headRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
        body.pitch = state.angles.bodyRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        body.yaw = state.angles.bodyRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        body.roll = state.angles.bodyRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
        leftArm.pitch = state.angles.leftArmRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        leftArm.yaw = state.angles.leftArmRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        leftArm.roll = state.angles.leftArmRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
        rightArm.pitch = state.angles.rightArmRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        rightArm.yaw = state.angles.rightArmRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        rightArm.roll = state.angles.rightArmRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
        leftLeg.pitch = state.angles.leftLegRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        leftLeg.yaw = state.angles.leftLegRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        leftLeg.roll = state.angles.leftLegRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
        rightLeg.pitch = state.angles.rightLegRotation.pitch() * MathHelper.RADIANS_PER_DEGREE;
        rightLeg.yaw = state.angles.rightLegRotation.yaw() * MathHelper.RADIANS_PER_DEGREE;
        rightLeg.roll = state.angles.rightLegRotation.roll() * MathHelper.RADIANS_PER_DEGREE;
    }
}
