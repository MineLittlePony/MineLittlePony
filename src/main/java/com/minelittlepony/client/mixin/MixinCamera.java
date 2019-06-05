package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.IPony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public abstract class MixinCamera {

    // cameraDistance;
    // float field_18721;
    // prevCameraDistance;
    // float field_18722;

    @Inject(method = "method_19318(D)D",
            at = @At("RETURN"),
            cancellable = true)
    private void redirectCameraDistance(double initial, CallbackInfoReturnable<Double> info) {
        double value = info.getReturnValueD();

        IPony pony = MineLittlePony.getInstance().getManager().getPony(MinecraftClient.getInstance().player);

        if (!pony.getRace(false).isHuman()) {
            value *= pony.getMetadata().getSize().getEyeDistanceFactor();
        }

        info.setReturnValue(value);
    }
}
