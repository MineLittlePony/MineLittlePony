package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

/**
 * Unmojang this code so the game doesn't crash
 * any time you try to create a union with multiples of the same vertex consumer.
 */
@Mixin(VertexMultiConsumer.class)
abstract class MixinVertextConsumers {
    private static final String T = "Lcom/mojang/blaze3d/vertex/VertexConsumer;";

    @Inject(method = "create(" + T + T + ")" + T, at = @At("HEAD"), cancellable = true)
    private static void onUnion(VertexConsumer first, VertexConsumer second, CallbackInfoReturnable<VertexConsumer> info) {
        if (first == second) {
            info.setReturnValue(first);
        }
    }

    @ModifyVariable(method = "create([" + T + ")" + T, at = @At("HEAD"), argsOnly = true)
    private static VertexConsumer[] onUnion(VertexConsumer[] delegates) {
        Set<VertexConsumer> set = new ObjectArraySet<>(delegates.length);
        for (VertexConsumer delegate : delegates) {
            set.add(delegate);
        }
        return set.toArray(VertexConsumer[]::new);
    }
}
