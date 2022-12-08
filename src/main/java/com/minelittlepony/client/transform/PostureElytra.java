package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import com.minelittlepony.api.model.IModel;

import net.minecraft.entity.LivingEntity;

public class PostureElytra implements PonyPosture<LivingEntity> {
    @Override
    public void transform(IModel model, LivingEntity entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        stack.translate(0, model.getAttributes().isCrouching ? 0.2F : -1, 0);
    }
}
