package com.minelittlepony.api.model.armour;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;

import java.util.*;

public interface ArmorModelRegistry {
    static final Map<Identifier, Optional<ModelKey<PonyArmourModel<?>>>> REGISTRY = new HashMap<>();

    public static Optional<ModelKey<PonyArmourModel<?>>> getModelKey(Item item, ArmourLayer layer) {
        Identifier id = Registries.ITEM.getId(item);
        if (id.getNamespace().equals("minecraft")) {
            return Optional.empty();
        }
        return REGISTRY.computeIfAbsent(id.withPath(p -> "models/armor/" + layer.name().toLowerCase() + "_" + p + ".json"), i -> {
            return Optional.of(Mson.getInstance().registerModel(i, PonyArmourModel::new));
        }).filter(key -> key.getModelData().isPresent());
    }
}
