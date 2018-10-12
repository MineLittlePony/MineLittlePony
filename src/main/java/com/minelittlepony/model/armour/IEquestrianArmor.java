package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelBiped;

import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.model.capabilities.IModelWrapper;

public interface IEquestrianArmor<V extends ModelBiped & IModelArmor> extends IModelWrapper {
    V getArmorForLayer(ArmorLayer layer);

    enum ArmorLayer {
        INNER,
        OUTER
    }
}
