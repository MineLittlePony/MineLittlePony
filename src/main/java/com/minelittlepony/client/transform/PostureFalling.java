package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;

public class PostureFalling implements PonyPosture<LivingEntity> {
    @Override
    public void transform(IModel model, LivingEntity entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.getAttributes().motionPitch = 0;
    }
}
