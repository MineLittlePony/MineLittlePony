package com.minelittlepony.client.model.armour;

import net.minecraft.item.Item;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.util.ResourceUtil;

import java.util.*;

public interface ArmorModelRegistry {
    static final Map<Identifier, Optional<ModelKey<AbstractPonyModel<?>>>> REGISTRY = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static ModelKey<AbstractPonyModel<?>> getModelKey(Item item, EquipmentModel.LayerType layerType, ArmourVariant variant) {
        return item.getRegistryEntry().getKey().map(key -> key.getValue()).flatMap(id -> {
            if (id.getNamespace().equals("minecraft")) {
                return Optional.empty();
            }
            return REGISTRY.computeIfAbsent(id.withPath(p -> ResourceUtil.format("armor/%s_%s.json", layerName(layerType), p)), i -> {
                return Optional.of(Mson.getInstance().registerModel(i, PonyArmourModel::new));
            }).filter(key -> key.getModelData().isPresent());
        }).orElse(variant.getDefaultModel(layerType));
    }

    private static String layerName(EquipmentModel.LayerType layerType) {
        return switch (layerType) {
            case HUMANOID -> "outer";
            case HUMANOID_LEGGINGS -> "inner";
            default -> layerType.name().toLowerCase(Locale.ROOT);
        };
    }
}
