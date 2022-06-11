package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.armour.ArmourLayer;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.mson.api.MsonModel;

public record ArmourWrapper<T extends LivingEntity> (
        PonyArmourModel<T> outerLayer,
        PonyArmourModel<T> innerLayer
    ) implements IArmour<PonyArmourModel<T>> {
    public static <T extends LivingEntity> ArmourWrapper<T> of(MsonModel.Factory<PonyArmourModel<T>> supplier) {
        return new ArmourWrapper<>(
                ModelType.ARMOUR_OUTER.createModel(supplier),
                ModelType.ARMOUR_INNER.createModel(supplier)
        );
    }

    @Override
    public ArmourWrapper<T> applyMetadata(IPonyData meta) {
        outerLayer.setMetadata(meta);
        innerLayer.setMetadata(meta);
        return this;
    }

    @Override
    public PonyArmourModel<T> getModel(ArmourLayer layer) {
        return layer == ArmourLayer.INNER ? innerLayer : outerLayer;
    }
}
