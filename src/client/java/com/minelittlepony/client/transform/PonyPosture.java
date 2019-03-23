package com.minelittlepony.client.transform;

import net.minecraft.entity.EntityLivingBase;

import com.minelittlepony.client.model.IClientModel;

public interface PonyPosture<T extends EntityLivingBase> {
    PonyPosture<EntityLivingBase> ELYTRA = new PostureElytra();
    PonyPosture<? extends EntityLivingBase> FLIGHT = new PostureFlight();
    PonyPosture<? extends EntityLivingBase> SWIMMING = new PostureSwimming();
    PonyPosture<EntityLivingBase> FALLING = new PostureFalling();

    default boolean applies(EntityLivingBase entity) {
        return true;
    }

    void transform(IClientModel model, T entity, double motionX, double motionY, double motionZ, float yaw, float ticks);
}
