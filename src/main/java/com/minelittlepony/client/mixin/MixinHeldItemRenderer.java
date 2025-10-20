package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
