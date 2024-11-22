package com.minelittlepony.client.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.EquineRenderManager.RegistrationHandler;
import com.minelittlepony.client.render.EquineRenderManager.SyncedPony;

@Mixin(PlayerEntity.class)
abstract class MixinPlayerEntity implements RegistrationHandler {
    private final SyncedPony syncedPony = new SyncedPony();

    @Override
    public SyncedPony getSyncedPony() {
        return syncedPony;
    }

    @ModifyReturnValue(method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"))
    private EntityDimensions modifyEyeHeight(EntityDimensions dimensions, EntityPose pose) {
        float factor = syncedPony.getCachedPonyData().size().eyeHeightFactor();
        return factor == 1 ? dimensions : dimensions.withEyeHeight(dimensions.eyeHeight() * factor);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        syncedPony.synchronize((PlayerEntity)(Object)this);
    }
}
