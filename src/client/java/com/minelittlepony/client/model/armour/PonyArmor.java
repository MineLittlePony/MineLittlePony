package com.minelittlepony.client.model.armour;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPonyData;

public class PonyArmor<T extends LivingEntity> implements IEquestrianArmour<ModelPonyArmor<T>> {

    private final ModelPonyArmor<T> outerLayer;
    private final ModelPonyArmor<T> innerLayer;

    public PonyArmor(ModelPonyArmor<T> outer, ModelPonyArmor<T> inner) {
        outerLayer = outer;
        innerLayer = inner;
    }

    @Override
    public void apply(IPonyData meta) {
        outerLayer.apply(meta);
        innerLayer.apply(meta);
    }

    @Override
    public void init() {
        outerLayer.init(0, 1.05F);
        innerLayer.init(0, 0.5F);
    }

    @Override
    public ModelPonyArmor<T> getArmorForLayer(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
