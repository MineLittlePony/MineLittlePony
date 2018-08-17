package com.minelittlepony.model.armour;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.model.armour.IEquestrianArmor.ArmorLayer;

import javax.annotation.Nullable;

public interface IArmorTextureResolver<T extends EntityLivingBase> {

    ResourceLocation getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmorLayer layer,  @Nullable String type);
}
