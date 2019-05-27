package com.minelittlepony.client.transform;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.client.model.ClientPonyModel;

public interface PonyPosture<T extends LivingEntity> {
    PonyPosture<LivingEntity> ELYTRA = new PostureElytra();
    PonyPosture<PlayerEntity> FLIGHT = new PostureFlight();
    PonyPosture<PlayerEntity> SWIMMING = new PostureSwimming();
    PonyPosture<LivingEntity> FALLING = new PostureFalling();

    default boolean applies(LivingEntity entity) {
        return true;
    }

    void transform(ClientPonyModel<?> model, T entity, double motionX, double motionY, double motionZ, float yaw, float ticks);
}
