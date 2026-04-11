package com.minelittlepony.client.model.armour;

import net.minecraft.client.resources.model.EquipmentClientInfo;

import com.minelittlepony.client.model.*;
import com.minelittlepony.mson.api.ModelKey;

public enum ArmourVariant {
    NORMAL(ModelType.INNER_PONY_ARMOR, ModelType.OUTER_PONY_ARMOR),
    LEGACY(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR),
    TRIM(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR);

    private final ModelKey<ClientPonyModel<?>> innerModel;
    private final ModelKey<ClientPonyModel<?>> outerModel;

    ArmourVariant(ModelKey<ClientPonyModel<?>> inner, ModelKey<ClientPonyModel<?>> outer) {
        this.innerModel = inner;
        this.outerModel = outer;
    }

    public ModelKey<ClientPonyModel<?>> getDefaultModel(EquipmentClientInfo.LayerType layerType) {
        return layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? innerModel : outerModel;
    }
}
