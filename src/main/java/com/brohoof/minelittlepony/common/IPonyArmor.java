package com.brohoof.minelittlepony.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPonyArmor {

    String getArmorTexture(EntityLivingBase e, ItemStack item, String def, int slot, String type);

    ModelBase getArmorModel(EntityLivingBase e, ItemStack item, int slot, ModelBase def);
}
