package net.minecraftforge.client;

import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

// stub
public class ForgeHooksClient {

    public static String getArmorTexture(Entity entity, ItemStack armor, String def, EntityEquipmentSlot slot, String type) {
        return def;
    }

    public static ModelBiped getArmorModel(EntityLivingBase entity, ItemStack item, EntityEquipmentSlot slot, ModelBiped def) {
        return def;
    }

}
