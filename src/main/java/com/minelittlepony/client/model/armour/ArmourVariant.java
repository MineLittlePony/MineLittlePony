package com.minelittlepony.client.model.armour;

import net.minecraft.client.render.entity.equipment.EquipmentModel;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.mson.api.ModelKey;

public enum ArmourVariant {
    NORMAL(ModelType.INNER_PONY_ARMOR, ModelType.OUTER_PONY_ARMOR),
    LEGACY(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR),
    TRIM(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR);

    private final ModelKey<AbstractPonyModel<?>> innerModel;
    private final ModelKey<AbstractPonyModel<?>> outerModel;

    ArmourVariant(ModelKey<AbstractPonyModel<?>> inner, ModelKey<AbstractPonyModel<?>> outer) {
        this.innerModel = inner;
        this.outerModel = outer;
    }

    public ModelKey<AbstractPonyModel<?>> getDefaultModel(EquipmentModel.LayerType layerType) {
        return layerType == EquipmentModel.LayerType.HUMANOID_LEGGINGS ? innerModel : outerModel;
    }
}
