package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumers;

/**
 * Unmojang this code so the game doesn't crash
 * any time you try to create a union with multiples of the same vertex consumer.
 */
@Mixin(VertexConsumers.class)
abstract class MixinVertextConsumers {
    private static final String T = "Lnet/minecraft/client/render/VertexConsumer;";

    @Inject(method = "union(" + T + T + ")" + T, at = @At("HEAD"), cancellable = true)
    private static void onUnion(VertexConsumer first, VertexConsumer second, CallbackInfoReturnable<VertexConsumer> info) {
        if (first == second) {
            info.setReturnValue(first);
        }
    }

    @Inject(method = "union([" + T + ")" + T, at = @At("HEAD"), cancellable = true)
    private static void onUnion(VertexConsumer[] delegates, CallbackInfoReturnable<VertexConsumer> info) {
        int oldLength = delegates.length;
        delegates = Arrays.stream(delegates).distinct().toArray(VertexConsumer[]::new);

        if (delegates.length == 1) {
            info.setReturnValue(delegates[0]);
        } else if (delegates.length != oldLength) {
            info.setReturnValue(new VertexConsumers.Union(delegates));
        }
    }
}
