package com.minelittlepony;

import com.mumfrey.liteloader.util.ModUtilities;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class ForgeProxy {

    private static boolean forgeLoaded = ModUtilities.fmlIsPresent();

    public static String getArmorTexture(Entity entity, ItemStack armor, String def, EntityEquipmentSlot slot, @Nullable String type) {
        if (forgeLoaded)
            return ForgeHooksClient.getArmorTexture(entity, armor, def, slot, type);
        return def;
    }

    public static ModelBiped getArmorModel(EntityLivingBase entity, ItemStack item, EntityEquipmentSlot slot, ModelBiped def) {
        if (forgeLoaded)
            return ForgeHooksClient.getArmorModel(entity, item, slot, def);
        return def;
    }

    public static Optional<Function<RenderManager,LayerRenderer<EntityPlayer>>> createShoulderLayer() {
        if (forgeLoaded) {
            // TODO
        }
        return Optional.empty();
    }

}
