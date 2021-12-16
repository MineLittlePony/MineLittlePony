package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.LevitatingItemRenderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(ItemRenderer.class)
abstract class MixinItemRenderer {
    private static final String VertexConsumerProvider = "Lnet/minecraft/client/render/VertexConsumerProvider;";
    private static final String VertexConsumer = "Lnet/minecraft/client/render/VertexConsumer;";
    private static final String RenderLayer = "Lnet/minecraft/client/render/RenderLayer;";

    private static final String Boolean = "Z";

    private static final String PARAMS = "(" + VertexConsumerProvider + RenderLayer + Boolean + Boolean + ")" + VertexConsumer;

    @Inject(method = {
          "getArmorGlintConsumer" + PARAMS,
          "getItemGlintConsumer" + PARAMS,
          "getDirectItemGlintConsumer" + PARAMS
        }, at = @At("HEAD"), cancellable = true)
    private static void onGetArmorVertexConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> info) {
        if (LevitatingItemRenderer.isEnabled()) {
            info.setReturnValue(provider.getBuffer(LevitatingItemRenderer.getRenderLayer()));
        }
    }
}
