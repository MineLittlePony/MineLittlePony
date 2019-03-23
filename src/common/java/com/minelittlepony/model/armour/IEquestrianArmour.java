package com.minelittlepony.model.armour;

import com.minelittlepony.model.capabilities.IModelArmour;
import com.minelittlepony.model.capabilities.IModelWrapper;

public interface IEquestrianArmour<V extends IModelArmour> extends IModelWrapper {
    /**
     * Gets the armour model to render for the given layer.
     */
    V getArmorForLayer(ArmourLayer layer);
}
