package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minelittlepony.client.render.LevitatingItemRenderer;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

@Mixin(HeldItemRenderer.class)
abstract class MixinHeldItemRenderer {
    @WrapOperation(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
             at = @At(value = "INVOKE",
                      target = "net/minecraft/client/render/item/ItemRenderState.render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V"))
    private void wrapRenderItem(ItemRenderState state,
            MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, int outline, Operation<Void> operation, LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode) {
        LevitatingItemRenderer.renderItem(entity, stack, renderMode, state, matrices, queue, light, overlay, outline, operation);
    }

    @Inject(method = "renderFirstPersonMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/render/command/OrderedRenderCommandQueue.submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V"))
    private void onRenderMap(MatrixStack matrices, OrderedRenderCommandQueue queue, int swingProgress, ItemStack stack, CallbackInfo info) {
        LevitatingItemRenderer.renderMap(matrices, queue, swingProgress, stack);
    }
}
