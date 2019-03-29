package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.client.PonyRenderManager;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation") // ItemCameraTransforms is deprecated by forge but we still need it.
@Mixin(FirstPersonRenderer.class)
public class MixinFirstPersonRenderer {

    @Redirect(method = "renderItemInFirstPerson("
                + "Lnet/minecraft/client/entity/AbstractClientPlayer;FF"
                + "Lnet/minecraft/util/EnumHand;F"
                + "Lnet/minecraft/item/ItemStack;F)V",
             at = @At(value = "INVOKE",
                      target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderItemSide("
                                  + "Lnet/minecraft/entity/EntityLivingBase;"
                                  + "Lnet/minecraft/item/ItemStack;"
                                  + "Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Z)V"))
    private void redirectRenderItemSide(ItemRenderer self, EntityLivingBase entity, ItemStack stack, TransformType transform, boolean left) {
        PonyRenderManager.getInstance().getMagicRenderer().renderItemInFirstPerson(self, (AbstractClientPlayer)entity, stack, transform, left);
    }
}
