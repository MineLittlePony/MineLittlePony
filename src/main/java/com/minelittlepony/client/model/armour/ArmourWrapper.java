package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPonyData;

import java.util.function.Supplier;

public class ArmourWrapper<T extends LivingEntity> implements IEquestrianArmour<ModelPonyArmour<T>> {

    private final ModelPonyArmour<T> outerLayer;
    private final ModelPonyArmour<T> innerLayer;

    public ArmourWrapper(Supplier<ModelPonyArmour<T>> supplier) {
        outerLayer = ModelType.ARMOUR_INNER.createModel(supplier);
        innerLayer = ModelType.ARMOUR_OUTER.createModel(supplier);
    }

    @Override
    public ArmourWrapper<T> apply(IPonyData meta) {
        outerLayer.apply(meta);
        innerLayer.apply(meta);
        return this;
    }

    @Override
    public ModelPonyArmour<T> getArmorForLayer(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
