package com.minelittlepony;

import com.mumfrey.liteloader.util.ModUtilities;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public class ForgeProxy {

    private static boolean forgeLoaded = ModUtilities.fmlIsPresent();

    public static String getArmorTexture(Entity entity, ItemStack armor, String def, EntityEquipmentSlot slot, String type) {
        if (forgeLoaded)
            return ForgeHooksClient.getArmorTexture(entity, armor, def, slot, type);
        return def;
    }

    public static ModelBiped getArmorModel(EntityLivingBase entity, ItemStack item, EntityEquipmentSlot slot, ModelBiped def) {
        if (forgeLoaded)
            return ForgeHooksClient.getArmorModel(entity, item, slot, def);
        return def;
    }

}
