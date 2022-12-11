package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.api.pony.IPony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
abstract class MixinCamera {
    @Inject(method = "clipToSpace(D)D",
            at = @At("RETURN"),
            cancellable = true)
    private void redirectCameraDistance(double initial, CallbackInfoReturnable<Double> info) {
        double value = info.getReturnValueD();

        IPony pony = IPony.getManager().getPony(MinecraftClient.getInstance().player);

        if (!pony.race().isHuman()) {
            value *= pony.metadata().getSize().getEyeDistanceFactor();
        }

        info.setReturnValue(value);
    }
}
