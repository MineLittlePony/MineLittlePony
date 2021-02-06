package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.google.common.base.Strings;
import com.minelittlepony.api.model.armour.ArmourLayer;
import com.minelittlepony.api.model.armour.ArmourVariant;
import com.minelittlepony.api.model.armour.IArmourTextureResolver;
import com.minelittlepony.util.ResourceUtil;

import javax.annotation.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DefaultArmourTextureResolver implements IArmourTextureResolver {

    private final Map<String, Identifier> HUMAN_ARMOUR = new HashMap<>();
    private final Map<Identifier, Identifier> PONY_ARMOUR = new HashMap<>();

    @Override
    public Identifier getTexture(LivingEntity entity, ItemStack itemstack, EquipmentSlot slot, ArmourLayer layer,  @Nullable String type) {
        type = Strings.nullToEmpty(type);

        ArmorItem item = (ArmorItem) itemstack.getItem();
        String texture = item.getMaterial().getName();

        String domain = "minecraft";

        int idx = texture.indexOf(':');
        if (idx > -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        String customType = type.isEmpty() ? "" : "_" + type;

        String res = ResourceUtil.format("%s:textures/models/armor/%s_layer_%s%s.png", domain, texture, layer, customType);
        String oldRes = ResourceUtil.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, layer.getLegacyId(), customType);

        Identifier human = getArmorTexture(res, type);
        Identifier pony = ponifyResource(human);

        Identifier oldHuman = getArmorTexture(oldRes, type);
        Identifier oldPony = ponifyResource(oldHuman);

        return resolve(oldPony, pony, oldHuman, human);
    }

    private Identifier resolve(Identifier... resources) {
        // check resource packs for either texture.

        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

        for (Identifier i : resources) {
            if (manager.containsResource(i)) {
                return i;
            }
        }

        return resources[resources.length - 1];
    }

    private Identifier ponifyResource(Identifier human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = key.getNamespace();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }

            return new Identifier(domain, key.getPath().replace(".png", "_pony.png"));
        });
    }

    private Identifier getArmorTexture(String def, String type) {
        return HUMAN_ARMOUR.computeIfAbsent(def + "#" + type, s -> {
            Identifier defId = new Identifier(def);

            if (type.isEmpty() || type.equals(def)) {
                return defId;
            }

            Identifier modId = new Identifier(type);

            Path modPath = Paths.get(modId.getPath()).getParent();

            if (modPath == null) {
                return defId;
            }

            Path path = modPath.resolve(Paths.get(defId.getPath()).getFileName());

            Identifier interemId = new Identifier(modId.getNamespace(), path.toString().replace('\\', '/'));

            if (MinecraftClient.getInstance().getResourceManager().containsResource(interemId)) {
                return interemId;
            }

            return modId;
        });
    }

    @Override
    public ArmourVariant getVariant(ArmourLayer layer, Identifier resolvedTexture) {
        if (resolvedTexture.getPath().endsWith("_pony.png")) {
            return ArmourVariant.NORMAL;
        }
        return ArmourVariant.LEGACY;
    }
}
