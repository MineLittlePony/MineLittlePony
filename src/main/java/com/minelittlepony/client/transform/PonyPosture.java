package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.model.IModel;

public interface PonyPosture<T extends LivingEntity> {

    PonyPosture<LivingEntity> DEFAULT = new PostureStanding();;
    PonyPosture<LivingEntity> ELYTRA = new PostureElytra();
    PonyPosture<PlayerEntity> FLIGHT = new PostureFlight();
    PonyPosture<PlayerEntity> SWIMMING = new PostureSwimming();
    PonyPosture<LivingEntity> FALLING = new PostureFalling();

    default boolean applies(LivingEntity entity) {
        return true;
    }

    default void apply(T player, IModel model, MatrixStack stack, float yaw, float ticks, int invert) {
        if (applies(player)) {
            double motionX = player.getX() - player.prevX;
            double motionY = player.method_24828() ? 0 : player.getY() - player.prevY;
            double motionZ = player.getZ() - player.prevZ;

            transform(model, player, stack, motionX, invert * motionY, motionZ, yaw, ticks);
        }
    }

    void transform(IModel model, T entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks);
}
