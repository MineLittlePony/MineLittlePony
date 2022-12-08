package com.minelittlepony.client;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.client.transform.PonyTransformation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PonyBounds {
    public static Vec3d getAbsoluteRidingOffset(LivingEntity entity) {
        return PonyPosture.getMountPony(entity).map(ridingPony -> {
            LivingEntity vehicle = (LivingEntity)entity.getVehicle();

            Vec3d offset = PonyTransformation.forSize(ridingPony.metadata().getSize()).getRiderOffset();
            float scale = ridingPony.metadata().getSize().getScaleFactor();

            return getAbsoluteRidingOffset(vehicle).add(
                    0,
                    offset.y - vehicle.getHeight() * 1 / scale,
                    0
            );
        }).orElseGet(() -> getBaseRidingOffset(entity));
    }

    private static Vec3d getBaseRidingOffset(LivingEntity entity) {
        float delta = MinecraftClient.getInstance().getTickDelta();

        Entity vehicle = entity.getVehicle();
        double vehicleOffset = vehicle == null ? 0 : vehicle.getHeight() - vehicle.getMountedHeightOffset();

        return new Vec3d(
                MathHelper.lerp(delta, entity.prevX, entity.getX()),
                MathHelper.lerp(delta, entity.prevY, entity.getY()) + vehicleOffset,
                MathHelper.lerp(delta, entity.prevZ, entity.getZ())
        );
    }

    public static Box getBoundingBox(IPony pony, LivingEntity entity) {
        final float scale = pony.metadata().getSize().getScaleFactor() + 0.1F;
        final float width = entity.getWidth() * scale;
        final float height = entity.getHeight() * scale;

        return new Box(-width, height, -width, width, 0, width).offset(getAbsoluteRidingOffset(entity));
    }
}
