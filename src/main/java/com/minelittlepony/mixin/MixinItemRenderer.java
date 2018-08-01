package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.MineLittlePony;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    private static final String AbstractClientPlayer = "Lnet/minecraft/client/entity/AbstractClientPlayer;";
    private static final String ItemStack = "Lnet/minecraft/item/ItemStack;";
    private static final String EnumHand = "Lnet/minecraft/util/EnumHand;";
    private static final String EntityLivingBase = "Lnet/minecraft/entity/EntityLivingBase;";
    private static final String TransformType = "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";

    //public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_)
    //public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded)
    @Redirect(method = "renderItemInFirstPerson(" + AbstractClientPlayer + "FF" + EnumHand + "F" + ItemStack + "F)V",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(" + EntityLivingBase + ItemStack + TransformType + "Z)V"))
    private void redirectRenderItemSide(ItemRenderer self, EntityLivingBase entity, ItemStack stack, TransformType transform, boolean left) {
        MineLittlePony.getInstance().getRenderManager().getMagicRenderer().renderItemInFirstPerson(self, (AbstractClientPlayer)entity, stack, transform, left);
    }
}
