package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minelittlepony.client.MineLittlePony;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.world.World;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(HeldItemRenderer.class)
abstract class MixinHeldItemRenderer {
    private static final String LivingEntity = "Lnet/minecraft/entity/LivingEntity;";
    private static final String MatrixStack = "Lnet/minecraft/client/util/math/MatrixStack;";
    private static final String ItemStack = "Lnet/minecraft/item/ItemStack;";
    private static final String Mode = "Lnet/minecraft/item/ModelTransformationMode;";
    private static final String VertexConsumerProvider = "Lnet/minecraft/client/render/VertexConsumerProvider;";
    private static final String World = "Lnet/minecraft/world/World;";
    private static final String ItemRenderer = "Lnet/minecraft/client/render/item/ItemRenderer;";

    private static final String Boolean = "Z";
    private static final String Int = "I";

    @WrapOperation(method = "renderItem(" + LivingEntity + ItemStack + Mode + Boolean + MatrixStack + VertexConsumerProvider + Int + ")V",
             at = @At(value = "INVOKE",
                      target = ItemRenderer + "renderItem(" + LivingEntity + ItemStack + Mode + Boolean + MatrixStack + VertexConsumerProvider + World + Int + Int + Int + ")V"))
    private void wrapRenderItem(ItemRenderer target,
            @Nullable LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode mode,
            boolean left,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            @Nullable World world,
            int light, int overlay, int seed, Operation<Void> operation) {


        if (!MineLittlePony.getInstance().getRenderDispatcher().getMagicRenderer().renderItem(target, entity, stack, mode, left, matrices, vertices, world, light, overlay, seed, operation)) {
            operation.call(target, entity, stack, mode, left, matrices, vertices, world, light, overlay, seed);
        }
    }
}
