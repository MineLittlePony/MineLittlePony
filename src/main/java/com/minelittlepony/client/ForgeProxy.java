package com.minelittlepony.client;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Proxy class for accessing forge fields and methods.
 */
public class ForgeProxy {

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
    public static String getArmorTexture(Entity entity, ItemStack item, String def, EquipmentSlot slot, @Nullable String type) {
        /*if (MineLPClient.getInstance().getModUtilities().hasFml())
            return ForgeHooksClient.getArmorTexture(entity, item, def, slot, type);*/
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
    public static <T extends LivingEntity> BipedEntityModel<T> getArmorModel(T entity, ItemStack item, EquipmentSlot slot, BipedEntityModel<T> def) {
        /*if (MineLPClient.getInstance().getModUtilities().hasFml()) {
            return ForgeHooksClient.getArmorModel(entity, item, slot, def);
        }*/
        return def;
    }
}
