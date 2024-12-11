package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.api.pony.Pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
abstract class MixinCamera {
    @ModifyReturnValue(method = "clipToSpace(F)F", at = @At("RETURN"))
    private float redirectCameraDistance(float value) {
        if (MinecraftClient.getInstance().player != null) {
            Pony pony = Pony.getManager().getPony(MinecraftClient.getInstance().player);

            if (!pony.race().isHuman()) {
                value *= pony.size().eyeDistanceFactor();
            }
        }
        return value;
    }
}
