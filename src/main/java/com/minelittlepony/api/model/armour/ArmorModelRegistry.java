package com.minelittlepony.api.model.armour;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.common.util.Untyped;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.util.ResourceUtil;

import java.util.*;

public interface ArmorModelRegistry {
    static final Map<Identifier, Optional<ModelKey<? extends PonyModel<?>>>> REGISTRY = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static ModelKey<PonyModel<?>> getModelKey(Item item, EquipmentClientInfo.LayerType layerType, ArmourVariant variant) {
        return Untyped.cast(item.builtInRegistryHolder().unwrapKey().map(ResourceKey::identifier)
                .filter(id -> !id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE))
                .flatMap(id -> REGISTRY.computeIfAbsent(id.withPath(p -> ResourceUtil.format("armor/%s_%s.json", layerName(layerType), p)),
                        i -> Optional.of(Mson.getInstance().registerModel(i, PonyArmourModel::new))))
                .filter(ModelKey::isBound)
                .orElse(variant.getDefaultModel(layerType)));
    }

    private static String layerName(EquipmentClientInfo.LayerType layerType) {
        return switch (layerType) {
            case HUMANOID -> "outer";
            case HUMANOID_LEGGINGS -> "inner";
            default -> layerType.name().toLowerCase(Locale.ROOT);
        };
    }
}
