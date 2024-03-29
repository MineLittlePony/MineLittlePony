package com.minelittlepony.client.mixin;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements EquineRenderManager.RegistrationHandler {
    public MixinClientPlayerEntity() { super(null, null); }

    private final EquineRenderManager.SyncedPony syncedPony = new EquineRenderManager.SyncedPony();

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("RETURN"))
    private void onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> info) {
        calculateDimensions();
    }

    @Inject(method = "dismountVehicle()V", at = @At("RETURN"))
    private void onStopRiding(CallbackInfo info) {
        calculateDimensions();
    }

    @Override
    public EquineRenderManager.SyncedPony getSyncedPony() {
        return syncedPony;
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        float value = super.getActiveEyeHeight(pose, dimensions);

        Pony pony = Pony.getManager().getPony(this);

        if (!pony.race().isHuman()) {
            float factor = pony.size().eyeHeightFactor();
            if (factor != 1) {
                value *= factor;

                if (hasVehicle()) {
                    value += getVehicle().getHeight();
                }
                return Math.max(value, 0.1F);
            }
        }

        return value;
    }
}
