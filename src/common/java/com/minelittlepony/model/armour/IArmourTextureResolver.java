package com.minelittlepony.model.armour;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IArmourTextureResolver<T extends EntityLivingBase> {

    /**
     * Gets the armour texture to be used for the given entity, armour piece, slot, and render layer.
     */
    ResourceLocation getArmourTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmourLayer layer,  @Nullable String type);
}
