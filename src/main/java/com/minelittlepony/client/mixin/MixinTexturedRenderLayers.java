package com.minelittlepony.client.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.LevitatingItemRenderer;

@Mixin(TexturedRenderLayers.class)
abstract class MixinTexturedRenderLayers {

    @Inject(method = "getEntityCutout()Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
    private static void onGetItemOpaque(CallbackInfoReturnable<RenderLayer> info) {
        if (LevitatingItemRenderer.isEnabled()) {
            info.setReturnValue(LevitatingItemRenderer.getRenderLayer());
        }
    }

    @Inject(method = "getEntityTranslucentCull()Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
    private static void onGetItemTranslucent(CallbackInfoReturnable<RenderLayer> info) {
        if (LevitatingItemRenderer.isEnabled()) {
            info.setReturnValue(LevitatingItemRenderer.getRenderLayer());
        }
    }
}
