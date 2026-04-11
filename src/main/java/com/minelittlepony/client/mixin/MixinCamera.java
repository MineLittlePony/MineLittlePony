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
    private Entity focusedEntity;

    @ModifyArg(method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 0)
    private float adjustCameraDistance(float value) {
        return value * minelp_getDistanceScale(focusedEntity);
    }

    @ModifyArg(method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 1)
    private float adjustVehicleCameraDistance(float value) {
        if (focusedEntity.isPassenger() && focusedEntity.getVehicle() instanceof LivingEntity l) {
            return value * minelp_getDistanceScale(l);
        }
        return value * minelp_getDistanceScale(focusedEntity);
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
