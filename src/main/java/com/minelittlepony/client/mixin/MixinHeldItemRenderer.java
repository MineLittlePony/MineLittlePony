package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;


@Mixin(ItemInHandRenderer.class)
abstract class MixinHeldItemRenderer {
    @WrapOperation(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
             at = @At(value = "INVOKE",
                      target = "net/minecraft/client/renderer/item/ItemStackRenderState.submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void wrapRenderItem(
            ItemStackRenderState target, /*.render(*/ PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, int outline, /*)*/ Operation<Void> operation,
            LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode) {
        LevitatingItemRenderer.renderItem(entity, stack, renderMode, target, matrices, frame, light, overlay, outline, operation);
    }

    @Inject(method = "renderMap(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/renderer/SubmitNodeCollector.submitCustomGeometry(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;)V"))
    private void onRenderMap(PoseStack matrices, SubmitNodeCollector frame, int light, ItemStack stack, CallbackInfo info) {
        LevitatingItemRenderer.renderMap(matrices, frame, stack);
    }
}
