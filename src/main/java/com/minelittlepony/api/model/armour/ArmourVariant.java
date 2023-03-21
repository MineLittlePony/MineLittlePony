package com.minelittlepony.api.model.armour;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.mson.api.ModelKey;

import java.util.Optional;

public enum ArmourVariant {
    NORMAL(ModelType.INNER_PONY_ARMOR, ModelType.OUTER_PONY_ARMOR),
    LEGACY(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR),
    TRIM(ModelType.INNER_VANILLA_ARMOR, ModelType.OUTER_VANILLA_ARMOR);

    private final Optional<ModelKey<PonyArmourModel<?>>> innerModel;
    private final Optional<ModelKey<PonyArmourModel<?>>> outerModel;

    ArmourVariant(ModelKey<PonyArmourModel<?>> inner, ModelKey<PonyArmourModel<?>> outer) {
        this.innerModel = Optional.of(inner);
        this.outerModel = Optional.of(outer);
    }

    public Optional<ModelKey<PonyArmourModel<?>>> getDefaultModel(ArmourLayer layer) {
        return layer.isInner() ? innerModel : outerModel;
    }
}
