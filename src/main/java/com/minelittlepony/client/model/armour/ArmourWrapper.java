package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.armour.ArmourLayer;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.mson.api.MsonModel;

public class ArmourWrapper<T extends LivingEntity> implements IArmour<PonyArmourModel<T>> {

    private final PonyArmourModel<T> outerLayer;
    private final PonyArmourModel<T> innerLayer;

    public ArmourWrapper(MsonModel.Factory<PonyArmourModel<T>> supplier) {
        outerLayer = ModelType.ARMOUR_OUTER.createModel(supplier);
        innerLayer = ModelType.ARMOUR_INNER.createModel(supplier);
    }

    @Override
    public ArmourWrapper<T> applyMetadata(IPonyData meta) {
        outerLayer.setMetadata(meta);
        innerLayer.setMetadata(meta);
        return this;
    }

    @Override
    public PonyArmourModel<T> getModel(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
