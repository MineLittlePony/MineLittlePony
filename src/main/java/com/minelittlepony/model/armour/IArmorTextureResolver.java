package com.minelittlepony.model.armour;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.model.armour.IEquestrianArmor.ArmorLayer;

import javax.annotation.Nullable;

public interface IArmorTextureResolver<T extends EntityLivingBase> {

    /**
     * Gets the armour texture to be used for the given entity, armour piece, slot, and render layer.
     */
    ResourceLocation getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmorLayer layer,  @Nullable String type);

    /**
     * Gets the armour variant for the identified texture.
     * Either normal for pony-style textures, or legacy for other textures.
     */
    default ArmourVariant getArmourVariant(ArmorLayer layer, ResourceLocation resolvedTexture) {
        return ArmourVariant.NORMAL;
    }
}
