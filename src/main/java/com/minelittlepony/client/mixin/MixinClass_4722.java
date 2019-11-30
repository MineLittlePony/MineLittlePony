package com.minelittlepony.client.mixin;

import net.minecraft.class_4722;
import net.minecraft.client.render.RenderLayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.LevitatingItemRenderer;

@Mixin(class_4722.class)
abstract class MixinClass_4722 {

    @Inject(method = "method_24074()Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
    private static void onGetItemOpaque(CallbackInfoReturnable<RenderLayer> info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            info.setReturnValue(LevitatingItemRenderer.getRenderLayer());
        }
    }

    @Inject(method = "method_24075()Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
    private static void onGetItemTranslucent(CallbackInfoReturnable<RenderLayer> info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            info.setReturnValue(LevitatingItemRenderer.getRenderLayer());
        }
    }
}
