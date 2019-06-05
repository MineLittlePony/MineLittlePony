package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.IPony;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    private MixinPlayerEntity() {super(null, null);}

    @Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntitySize;)F",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetActiveEyeHeight(EntityPose pose, EntitySize size, CallbackInfoReturnable<Float> info) {
        float value = info.getReturnValueF();

        IPony pony = MineLittlePony.getInstance().getManager().getPony((PlayerEntity)(Object)this);

        if (!pony.getRace(false).isHuman()) {
            value *= pony.getMetadata().getSize().getEyeHeightFactor();
        }

        info.setReturnValue(value);
    }
}
