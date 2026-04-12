package com.minelittlepony.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.minelittlepony.api.pony.Pony;


@Mixin(Camera.class)
abstract class MixinCamera {
    @Shadow
    private Entity entity;

    @ModifyArg(method = "alignWithEntity(F)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 0)
    private float adjustCameraDistance(float value) {
        return value * minelp_getDistanceScale(entity);
    }

    @ModifyArg(method = "alignWithEntity(F)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 1)
    private float adjustMountCameraDistance(float value) {
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity l) {
            return value * minelp_getDistanceScale(l);
        }
        return value * minelp_getDistanceScale(entity);
    }

    @Unique
    private float minelp_getDistanceScale(Entity entity) {
        Pony pony = Pony.getManager().getPony(entity).orElse(null);

        if (pony != null && !pony.race().isHuman()) {
            return pony.size().eyeDistanceFactor();
        }

        return 1;
    }
}
