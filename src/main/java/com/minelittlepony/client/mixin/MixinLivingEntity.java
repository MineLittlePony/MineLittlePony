package com.minelittlepony.client.mixin;

import net.minecraft.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.EquineRenderManager.RegistrationHandler;

@Mixin(LivingEntity.class)
abstract class MixinLivingEntity {
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (this instanceof RegistrationHandler handler) {
            handler.getSyncedPony().synchronize();
        }
    }
}