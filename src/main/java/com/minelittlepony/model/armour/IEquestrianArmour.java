package com.minelittlepony.model.armour;

import com.minelittlepony.model.IModelWrapper;

public interface IEquestrianArmour<V extends IArmour> extends IModelWrapper {
    /**
     * Gets the armour model to render for the given layer.
     */
    V getArmorForLayer(ArmourLayer layer);
}
