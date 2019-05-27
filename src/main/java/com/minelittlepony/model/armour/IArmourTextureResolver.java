package com.minelittlepony.model.armour;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface IArmourTextureResolver<T extends LivingEntity> {

    /**
     * Gets the armour texture to be used for the given entity, armour piece, slot, and render layer.
     */
    Identifier getArmourTexture(T entity, ItemStack itemstack, EquipmentSlot slot, ArmourLayer layer,  @Nullable String type);
}
