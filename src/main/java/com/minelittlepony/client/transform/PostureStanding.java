package com.minelittlepony.client.transform;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.IModel;

public class PostureStanding implements PonyPosture<LivingEntity> {
    @Override
    public boolean applies(LivingEntity entity) {
        return false;
    }

    @Override
    public void transform(IModel model, LivingEntity entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
    }
}
