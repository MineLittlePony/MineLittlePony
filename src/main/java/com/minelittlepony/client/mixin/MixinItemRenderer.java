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

    @Inject(method = "getArmorGlintConsumer(" + VertexConsumerProvider + RenderLayer + Boolean + Boolean + ")" + VertexConsumer, at = @At("HEAD"), cancellable = true)
    private static void onGetArmorVertexConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint, CallbackInfoReturnable<VertexConsumer> info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            info.setReturnValue(provider.getBuffer(LevitatingItemRenderer.getRenderLayer()));
        }
    }

    @Inject(method = "getItemGlintConsumer(" + VertexConsumerProvider + RenderLayer + Boolean + Boolean + ")" + VertexConsumer, at = @At("HEAD"), cancellable = true)
    private static void onMethod_29711(VertexConsumerProvider provider, RenderLayer layer, boolean solide, boolean glint, CallbackInfoReturnable<VertexConsumer> info) {
        if (LevitatingItemRenderer.usesTransparency()) {
            info.setReturnValue(provider.getBuffer(LevitatingItemRenderer.getRenderLayer()));
        }
    }
}
