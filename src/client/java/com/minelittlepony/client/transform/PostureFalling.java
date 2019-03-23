package com.minelittlepony.client.transform;

import net.minecraft.entity.EntityLivingBase;

import com.minelittlepony.client.model.IClientModel;

public class PostureFalling implements PonyPosture<EntityLivingBase> {
    @Override
    public void transform(IClientModel model, EntityLivingBase entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.setPitch(0);
    }
}
