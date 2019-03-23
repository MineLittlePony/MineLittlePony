package com.minelittlepony.model.armour;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

public class DefaultPonyArmorTextureResolver<T extends EntityLivingBase> implements IArmourTextureResolver<T> {

    private final Map<String, ResourceLocation> HUMAN_ARMOUR = Maps.newHashMap();
    private final Map<ResourceLocation, ResourceLocation> PONY_ARMOUR = Maps.newHashMap();

    @Override
    public ResourceLocation getArmourTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmourLayer layer,  @Nullable String type) {
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
        String oldPonyRes = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, layer == ArmourLayer.INNER ? 2 : 1, customType);

        ResourceLocation human = getArmorTexture(entity, itemstack, ponyRes, slot, type);
        ResourceLocation pony = ponifyResource(human);

        ResourceLocation oldHuman = getArmorTexture(entity, itemstack, oldPonyRes, slot, type);
        ResourceLocation oldPony = ponifyResource(oldHuman);

        return resolve(pony, oldPony, oldHuman, human);
    }

    private ResourceLocation resolve(ResourceLocation... resources) {
        // check resource packs for either texture.
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            for (ResourceLocation candidate : resources) {
                if (entry.getResourcePack().resourceExists(candidate)) {
                    // ponies are more important
                    return candidate;
                }
            }
        }

        // the default pack
        for (ResourceLocation candidate : resources) {
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(candidate);
                return candidate;
            } catch (IOException e) { }
        }

        return resources[resources.length - 1];
    }

    private ResourceLocation ponifyResource(ResourceLocation human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = human.getNamespace();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }

            return new ResourceLocation(domain, human.getPath().replace(".png", "_pony.png"));
        });
    }

    private ResourceLocation getArmorTexture(T entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {
        return HUMAN_ARMOUR.computeIfAbsent(ForgeProxy.getArmorTexture(entity, item, def, slot, type), ResourceLocation::new);
    }
}








