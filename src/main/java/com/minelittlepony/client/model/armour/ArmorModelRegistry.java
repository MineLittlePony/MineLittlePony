package com.minelittlepony.client.model.armour;

import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;

import java.util.*;

public interface ArmorModelRegistry {
    static final Map<Identifier, Optional<ModelKey<AbstractPonyModel<?>>>> REGISTRY = new HashMap<>();

    public static Optional<ModelKey<AbstractPonyModel<?>>> getModelKey(Item item, EquipmentModel.LayerType layerType) {
        Identifier id = Registries.ITEM.getId(item);
        if (id.getNamespace().equals("minecraft")) {
            return Optional.empty();
        }
        return REGISTRY.computeIfAbsent(id.withPath(p -> "armor/" + layerType.name().toLowerCase(Locale.ROOT) + "_" + p + ".json"), i -> {
            return Optional.of(Mson.getInstance().registerModel(i, PonyArmourModel::new));
        }).filter(key -> key.getModelData().isPresent());
    }
}
