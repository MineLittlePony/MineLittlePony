package com.minelittlepony;

import com.mumfrey.liteloader.util.ModUtilities;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;

/**
 * Proxy class for accessing forge fields and methods.
 */
public class ForgeProxy {

    /**
     * True if forge is present.
     */
    private static boolean forgeLoaded = ModUtilities.fmlIsPresent();

    /**
     * Gets the mod armour texture for an associated item and slot.
     *
     * @param entity    The entity to get armour for.
     * @param item      The armour item
     * @param def       Default return value if no mods present
     * @param slot      The slot this armour piece is place in.
     * @param type      unknown
     * @return
     */
    public static String getArmorTexture(Entity entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {
        if (forgeLoaded) {
            return ForgeHooksClient.getArmorTexture(entity, item, def, slot, type);
        }
        return def;
    }

    /**
     * Gets the mod armour model for an associated item and slot.
     *
     * @param entity    The entity to get armour for.
     * @param item      The armour item
     * @param slot      The slot this armour piece is place in.
     * @param def       Default return value if no mods present
     */
    public static ModelBiped getArmorModel(EntityLivingBase entity, ItemStack item, EntityEquipmentSlot slot, ModelBiped def) {
        if (forgeLoaded) {
            return ForgeHooksClient.getArmorModel(entity, item, slot, def);
        }
        return def;
    }
}
