package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.client.PonyRenderManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(FirstPersonRenderer.class)
public class MixinFirstPersonRenderer {

    @Redirect(method = "renderFirstPersonItem("
                + "Lnet/minecraft/client/network/AbstractClientPlayerEntity;FF"
                + "Lnet/minecraft/util/Hand;F"
                + "Lnet/minecraft/item/ItemStack;F)V",
             at = @At(value = "INVOKE",
                      target = "Lnet/minecraft/client/render/FirstPersonRenderer;renderItemFromSide("
                                  + "Lnet/minecraft/entity/LivingEntity;"
                                  + "Lnet/minecraft/item/ItemStack;"
                                  + "Lnet/minecraft/client/render/model/json/ModelTransformation$Type;Z)V"))
    private void redirectRenderItemSide(ItemRenderer self, LivingEntity entity, ItemStack stack, ModelTransformation.Type transform, boolean left) {
        PonyRenderManager.getInstance().getMagicRenderer().renderItemInFirstPerson(self, (AbstractClientPlayerEntity)entity, stack, transform, left);
    }
}
