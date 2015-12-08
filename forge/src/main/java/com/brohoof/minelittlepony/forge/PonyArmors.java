package com.brohoof.minelittlepony.forge;

import com.brohoof.minelittlepony.forge.IPonyArmor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PonyArmors implements IPonyArmor {

    @Override
    public String getArmorTexture(EntityLivingBase entity, ItemStack armor, String def, int slot, String type) {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result == null ? def : result;
    }

    @Override
    public ModelBase getArmorModel(EntityLivingBase entity, ItemStack item, int slot, ModelBase def) {
        ModelBase result = item.getItem().getArmorModel(entity, item, slot);
        return result == null ? def : result;
    }

}
