package com.minelittlepony.forge;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IForgeHooks {

    String getArmorTexture(EntityLivingBase e, ItemStack item, String def, int slot, String type);

    ModelBase getArmorModel(EntityLivingBase e, ItemStack item, int slot, ModelBase def);
}
