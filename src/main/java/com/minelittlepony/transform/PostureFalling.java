package com.minelittlepony.transform;

import com.minelittlepony.model.capabilities.IModel;

import net.minecraft.entity.EntityLivingBase;

public class PostureFalling implements PonyPosture<EntityLivingBase> {
    @Override
    public void transform(IModel model, EntityLivingBase entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.setPitch(0);
    }
}
