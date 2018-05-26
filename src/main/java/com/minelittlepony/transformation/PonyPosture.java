package com.minelittlepony.transformation;

import com.minelittlepony.model.AbstractPonyModel;

import net.minecraft.entity.EntityLivingBase;

public interface PonyPosture<T extends EntityLivingBase> {
    PonyPosture<EntityLivingBase> ELYTRA = new PostureElytra();
    PonyPosture<? extends EntityLivingBase> FLIGHT = new PostureFlight();
    PonyPosture<EntityLivingBase> FALLING = new PostureFalling();

    default boolean applies(EntityLivingBase entity) {
        return true;
    }

    void transform(AbstractPonyModel model, T entity, double motionX, double motionY, double motionZ, float pitch, float yaw, float ticks);
}
