package com.minelittlepony.model.armour;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;
import com.minelittlepony.model.armour.IEquestrianArmor.ArmorLayer;

import javax.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DefaultPonyArmorTextureResolver<T extends EntityLivingBase> implements IArmorTextureResolver<T> {

    private final Map<String, ResourceLocation> HUMAN_ARMOUR = Maps.newHashMap();
    private final Map<ResourceLocation, ResourceLocation> PONY_ARMOUR = Maps.newHashMap();

    @Override
    public ResourceLocation getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmorLayer layer,  @Nullable String type) {
        ItemArmor item = (ItemArmor) itemstack.getItem();
        String texture = item.getArmorMaterial().getName();

        String domain = "minecraft";

        int idx = texture.indexOf(':');
        if (idx > -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        String customType = type == null ? "" : String.format("_%s", type);

        String ponyRes = String.format("%s:textures/models/armor/%s_layer_%s%s.png", domain, texture, layer.name().toLowerCase(), customType);
        String oldPonyRes = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, layer == ArmorLayer.INNER ? 2 : 1, customType);

        ResourceLocation human = getArmorTexture(entity, itemstack, ponyRes, slot, type);
        ResourceLocation pony = ponifyResource(human);

        ResourceLocation oldHuman = getArmorTexture(entity, itemstack, oldPonyRes, slot, type);
        ResourceLocation oldPony = ponifyResource(oldHuman);

        return resolve(pony, oldPony, oldHuman, human);
    }

    private ResourceLocation resolve(ResourceLocation... resources) {
        for (ResourceLocation candidate : resources) {
            if (resourceExists(candidate)) {
                return candidate;
            }
        }

        return resources[resources.length - 1];
    }

    private boolean resourceExists(ResourceLocation resource) {
        try {
            return Minecraft.getMinecraft().getResourceManager().getResource(resource) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private ResourceLocation ponifyResource(ResourceLocation human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = key.getNamespace();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }

            return new ResourceLocation(domain, key.getPath().replace(".png", "_pony.png"));
        });
    }

    private ResourceLocation getArmorTexture(T entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {

        String modTexture = Strings.nullToEmpty(ForgeProxy.getArmorTexture(entity, item, def, slot, type));

        if (modTexture.isEmpty() || modTexture.equals(def)) {
            return HUMAN_ARMOUR.computeIfAbsent(def, ResourceLocation::new);
        }

        return HUMAN_ARMOUR.computeIfAbsent(modTexture, s -> {
            ResourceLocation modId = new ResourceLocation(s);
            ResourceLocation defId = new ResourceLocation(def);

            Path defPath = Paths.get(defId.getPath());

            String domain = modId.getNamespace();

            String path = Paths.get(modId.getPath()).getParent().resolve(defPath.getFileName()).toString().replace('\\', '/');;

            ResourceLocation interemId = new ResourceLocation(domain, path);

            if (resourceExists(interemId)) {
                return interemId;
            }

            return modId;
        });
    }

    @Override
    public ArmourVariant getArmourVariant(ArmorLayer layer, ResourceLocation resolvedTexture) {
        if (resolvedTexture.getPath().endsWith("_pony.png")) {
            return ArmourVariant.NORMAL;
        }
        return ArmourVariant.LEGACY;
    }
}








