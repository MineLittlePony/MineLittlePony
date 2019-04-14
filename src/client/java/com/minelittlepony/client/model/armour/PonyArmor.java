package com.minelittlepony.client.model.armour;

import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPonyData;

public class PonyArmor implements IEquestrianArmour<ModelPonyArmor> {

    private final ModelPonyArmor outerLayer;
    private final ModelPonyArmor innerLayer;

    public PonyArmor(ModelPonyArmor outer, ModelPonyArmor inner) {
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
    public ModelPonyArmor getArmorForLayer(ArmourLayer layer) {

        if (layer == ArmourLayer.INNER) {
            return innerLayer;
        }

        return outerLayer;
    }
}
