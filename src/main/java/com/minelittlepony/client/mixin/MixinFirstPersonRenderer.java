package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.client.PonyRenderManager;

import javax.annotation.Nullable;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(FirstPersonRenderer.class)
public class MixinFirstPersonRenderer {

    @Redirect(method = "renderFirstPersonItem("
                + "Lnet/minecraft/client/network/AbstractClientPlayerEntity;FF"
                + "Lnet/minecraft/util/Hand;F"
                + "Lnet/minecraft/item/ItemStack;F)V",
             at = @At(value = "INVOKE",
                      target = "Lnet/minecraft/client/render/item/ItemRenderer;method_23177("
                                  + "Lnet/minecraft/entity/LivingEntity;"
                                  + "Lnet/minecraft/item/ItemStack;"
                                  + "Lnet/minecraft/client/render/model/json/ModelTransformation$Type;"
                                  + "Z"
                                  + "Lnet/minecraft/client/util/math/MatrixStack;"
                                  + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                                  + "Lnet/minecraft/world/World;"
                                  + "I"
                                  + "I)V"))
    private void redirectRenderItemSide(FirstPersonRenderer self,
            @Nullable LivingEntity entity, ItemStack item, ModelTransformation.Type transform, boolean left,
            MatrixStack stack, VertexConsumerProvider renderContext, @Nullable World world, int lightUv, int overlayUv) {
        PonyRenderManager.getInstance().getMagicRenderer().renderItemInFirstPerson(self, (AbstractClientPlayerEntity)entity, item, transform, left, stack, renderContext, world, lightUv);
    }
}
