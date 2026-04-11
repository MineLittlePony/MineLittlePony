package com.minelittlepony.client.mixin;

import net.minecraft.world.entity.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.client.render.EquineRenderManager.RegistrationHandler;
import com.minelittlepony.client.render.EquineRenderManager.SyncedPony;

@Mixin(Avatar.class)
abstract class MixinPlayerEntity extends LivingEntity implements RegistrationHandler {
    MixinPlayerEntity() {super(null, null);}

    private final SyncedPony syncedPony = new SyncedPony((Avatar)(Object)this);

    @Override
    public SyncedPony getSyncedPony() {
        return syncedPony;
    }

    @ModifyReturnValue(method = "getDefaultDimensions", at = @At("RETURN"))
    private EntityDimensions modifyEyeHeight(EntityDimensions dimensions, Pose pose) {
        return getSyncedPony().modifyEyeHeight(dimensions, pose);
    }
}
