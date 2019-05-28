package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import com.google.common.collect.Maps;
import com.minelittlepony.client.ForgeProxy;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IArmourTextureResolver;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

public class DefaultArmourTextureResolver<T extends LivingEntity> implements IArmourTextureResolver<T> {

    private final Map<String, Identifier> HUMAN_ARMOUR = Maps.newHashMap();
    private final Map<Identifier, Identifier> PONY_ARMOUR = Maps.newHashMap();

    @Override
    public Identifier getArmourTexture(T entity, ItemStack itemstack, EquipmentSlot slot, ArmourLayer layer,  @Nullable String type) {
        ArmorItem item = (ArmorItem) itemstack.getItem();
        String texture = item.getMaterial().getName();

        String domain = "minecraft";

        int idx = texture.indexOf(':');
        if (idx > -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        String customType = type == null ? "" : String.format("_%s", type);

        String ponyRes = String.format("%s:textures/models/armor/%s_layer_%s%s.png", domain, texture, layer.name().toLowerCase(), customType);
        String oldPonyRes = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, layer == ArmourLayer.INNER ? 2 : 1, customType);

        Identifier human = getArmorTexture(entity, itemstack, ponyRes, slot, type);
        Identifier pony = ponifyResource(human);

        Identifier oldHuman = getArmorTexture(entity, itemstack, oldPonyRes, slot, type);
        Identifier oldPony = ponifyResource(oldHuman);

        return resolve(pony, oldPony, oldHuman, human);
    }

    private Identifier resolve(Identifier... resources) {
        // check resource packs for either texture.
        // TODO: We need another way to iterate loaded resourcepacks
        for (ClientResourcePackContainer entry : MinecraftClient.getInstance().getResourcePackContainerManager().getEnabledContainers()) {
            ResourcePack pack = entry.createResourcePack();
            for (Identifier candidate : resources) {
                if (pack.contains(ResourceType.CLIENT_RESOURCES, candidate)) {
                    // ponies are more important
                    return candidate;
                }
            }
        }

        // the default pack
        for (Identifier candidate : resources) {
            try {
                MinecraftClient.getInstance().getResourceManager().getResource(candidate);
                return candidate;
            } catch (IOException e) { }
        }

        return resources[resources.length - 1];
    }

    private Identifier ponifyResource(Identifier human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = human.getNamespace();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }

            return new Identifier(domain, human.getPath().replace(".png", "_pony.png"));
        });
    }

    private Identifier getArmorTexture(T entity, ItemStack item, String def, EquipmentSlot slot, @Nullable String type) {
        return HUMAN_ARMOUR.computeIfAbsent(ForgeProxy.getArmorTexture(entity, item, def, slot, type), Identifier::new);
    }
}








