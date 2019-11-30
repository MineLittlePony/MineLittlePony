package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.client.render.PonyRenderDispatcher;

import javax.annotation.Nullable;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation.Type;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(FirstPersonRenderer.class)
abstract class MixinFirstPersonRenderer {
    private static final String LivingEntity = "Lnet/minecraft/entity/LivingEntity;";
    private static final String MatrixStack = "Lnet/minecraft/client/util/math/MatrixStack;";
    private static final String ItemStack = "Lnet/minecraft/item/ItemStack;";
    private static final String Type = "Lnet/minecraft/client/render/model/json/ModelTransformation$Type;";
    private static final String VertexConsumerProvider = "Lnet/minecraft/client/render/VertexConsumerProvider;";
    private static final String World = "Lnet/minecraft/world/World;";
    private static final String ItemRenderer = "Lnet/minecraft/client/render/item/ItemRenderer;";

    private static final String Boolean = "Z";
    private static final String Int = "I";

    @Redirect(method = "renderItem(" + LivingEntity + ItemStack + Type + Boolean + MatrixStack + VertexConsumerProvider + Int + ")V",
             at = @At(value = "INVOKE",
                      target = ItemRenderer + "method_23177(" + LivingEntity + ItemStack + Type + Boolean + MatrixStack + VertexConsumerProvider + World + Int + Int + ")V"))
    private void redirectRenderItem(ItemRenderer target,
            @Nullable LivingEntity entity,
            ItemStack item,
            Type transform,
            boolean left,
            MatrixStack stack,
            VertexConsumerProvider renderContext,
            @Nullable World world,
            int lightUv, int overlayUv) {
        PonyRenderDispatcher.getInstance().getMagicRenderer().renderItemInFirstPerson(target, (AbstractClientPlayerEntity)entity, item, transform, left, stack, renderContext, world, lightUv);
    }
}
