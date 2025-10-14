package com.minelittlepony.client.mixin;

import net.minecraft.entity.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.EquineRenderManager.RegistrationHandler;
import com.minelittlepony.client.render.EquineRenderManager.SyncedPony;

@Mixin(PlayerLikeEntity.class)
abstract class MixinPlayerEntity implements RegistrationHandler {
    private final SyncedPony syncedPony = new SyncedPony((PlayerLikeEntity)(Object)this);

    @Override
    public SyncedPony getSyncedPony() {
        return syncedPony;
    }

    @ModifyReturnValue(method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"))
    private EntityDimensions modifyEyeHeight(EntityDimensions dimensions, EntityPose pose) {
        return getSyncedPony().modifyEyeHeight(dimensions, pose);
    }
}
