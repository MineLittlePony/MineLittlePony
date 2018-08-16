package com.minelittlepony.model.armour;

public interface IEquestrianArmor {
    ModelPonyArmor getArmorForLayer(ArmorLayer layer);

    enum ArmorLayer {
        INNER,
        OUTER
    }
}
