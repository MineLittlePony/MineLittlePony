package com.minelittlepony.client.mixin;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    public MixinClientPlayerEntity() { super(null, null); }

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("RETURN"))
    public void onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> info) {
        calculateDimensions();
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        calculateDimensions();
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        float value = super.getActiveEyeHeight(pose, dimensions);

        IPony pony = MineLittlePony.getInstance().getManager().getPony(this);

        if (!pony.getRace(false).isHuman()) {
            value *= pony.getMetadata().getSize().getEyeHeightFactor();

            if (hasVehicle()) {
                value += getVehicle().getEyeHeight(getVehicle().getPose());
                value -= getVehicle().getMountedHeightOffset();
            }
        }

        return Math.max(value, 0.3F);
    }
}
