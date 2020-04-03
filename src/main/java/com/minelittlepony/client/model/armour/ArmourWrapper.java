package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IEquestrianArmour;

import java.util.function.Supplier;

public class ArmourWrapper<T extends LivingEntity> implements IEquestrianArmour<PonyArmourModel<T>> {

    private final PonyArmourModel<T> outerLayer;
    private final PonyArmourModel<T> innerLayer;

    public ArmourWrapper(Supplier<PonyArmourModel<T>> supplier) {
        outerLayer = ModelType.ARMOUR_OUTER.createModel(supplier);
        innerLayer = ModelType.ARMOUR_INNER.createModel(supplier);
    }

    @Override
    public ArmourWrapper<T> apply(IPonyData meta) {
        outerLayer.apply(meta);
        innerLayer.apply(meta);
        return this;
    }

    @Override
    public PonyArmourModel<T> getArmorForLayer(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
