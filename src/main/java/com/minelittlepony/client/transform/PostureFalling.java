package com.minelittlepony.client.transform;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.IModel;

public class PostureFalling implements PonyPosture<LivingEntity> {
    @Override
    public void transform(IModel model, LivingEntity entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.getAttributes().motionPitch = 0;
    }
}
