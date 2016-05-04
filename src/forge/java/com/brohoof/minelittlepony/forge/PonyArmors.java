package com.brohoof.minelittlepony.forge;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class PonyArmors implements IPonyArmor {

    @Override
    public String getArmorTexture(EntityLivingBase entity, ItemStack armor, String def, int slot, String type) {
        return ForgeHooksClient.getArmorTexture(entity, armor, def, slot, type);
    }

    @Override
    public ModelBase getArmorModel(EntityLivingBase entity, ItemStack item, int slot, ModelBase def) {
        return ForgeHooksClient.getArmorModel(entity, item, slot, def);
    }

}
