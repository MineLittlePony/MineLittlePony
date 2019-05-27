package com.minelittlepony.client.transform;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.ClientPonyModel;

public class PostureFalling implements PonyPosture<LivingEntity> {
    @Override
    public void transform(ClientPonyModel<?> model, LivingEntity entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.setPitch(0);
    }
}
