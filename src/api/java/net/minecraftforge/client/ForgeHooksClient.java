package net.minecraftforge.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

// stub
public class ForgeHooksClient {

    public static String getArmorTexture(EntityLivingBase entity, ItemStack armor, String def, int slot, String type) {
        return def;
    }

    public static ModelBase getArmorModel(EntityLivingBase entity, ItemStack item, int slot, ModelBase def) {
        return def;
    }

}
