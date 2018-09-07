package com.minelittlepony.model.armour;

import com.minelittlepony.model.capabilities.IModelWrapper;
import com.minelittlepony.pony.data.IPonyData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PonyArmor implements IModelWrapper, IEquestrianArmor {

    public PonyArmor(ModelPonyArmor outer, ModelPonyArmor inner) {
        outerLayer = outer;
        innerLayer = inner;
    }

    @Override
    public void apply(IPonyData meta) {
        outerLayer.metadata = meta;
        innerLayer.metadata = meta;
    }

    @Override
    public void init() {
        outerLayer.init(0, 1.05F);
        innerLayer.init(0, 0.5F);
    }


    @Nullable
    public ModelPonyArmor outerLayer;
    @Nullable
    public ModelPonyArmor innerLayer;


    @Override @Nonnull
    public ModelPonyArmor getArmorForLayer(@Nullable ArmorLayer layer) {

        if (layer.ordinal() == ArmorLayer.INNER.ordinal()) {
            return innerLayer;
        }

        return null;
    }
}
