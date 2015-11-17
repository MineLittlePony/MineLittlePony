package com.brohoof.minelittlepony.gui;

import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;

public interface IPropertyProviderEnum<E extends Enum<E>> extends IVoxelPropertyProvider {

    void setProperty(String key, E val);

    E getEnumProperty(String key);
}
