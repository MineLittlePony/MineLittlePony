package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.client.render.PonyRenderDispatcher;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(HeldItemRenderer.class)
abstract class MixinHeldItemRenderer {
    private static final String LivingEntity = "Lnet/minecraft/entity/LivingEntity;";
    private static final String MatrixStack = "Lnet/minecraft/client/util/math/MatrixStack;";
    private static final String ItemStack = "Lnet/minecraft/item/ItemStack;";
    private static final String Mode = "Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;";
    private static final String VertexConsumerProvider = "Lnet/minecraft/client/render/VertexConsumerProvider;";
    private static final String World = "Lnet/minecraft/world/World;";
    private static final String ItemRenderer = "Lnet/minecraft/client/render/item/ItemRenderer;";

    private static final String Boolean = "Z";
    private static final String Int = "I";

    @Redirect(method = "renderItem(" + LivingEntity + ItemStack + Mode + Boolean + MatrixStack + VertexConsumerProvider + Int + ")V",
             at = @At(value = "INVOKE",
                      target = ItemRenderer + "renderItem(" + LivingEntity + ItemStack + Mode + Boolean + MatrixStack + VertexConsumerProvider + World + Int + Int + Int + ")V"))
    private void redirectRenderItem(ItemRenderer target,
            @Nullable LivingEntity entity,
            ItemStack item,
            ModelTransformation.Mode transform,
            boolean left,
            MatrixStack stack,
            VertexConsumerProvider renderContext,
            @Nullable World world,
            int lightUv, int overlayUv, int posLong) {
        PonyRenderDispatcher.getInstance().getMagicRenderer().renderItemInFirstPerson(target, entity, item, transform, left, stack, renderContext, world, lightUv, posLong);
    }
}
