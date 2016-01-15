package com.mumfrey.liteloader.client.util;

import java.util.Map;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.PrivateFields;

import net.minecraft.tileentity.TileEntity;

@SuppressWarnings("rawtypes")
public final class PrivateFieldsClient<P, T> extends PrivateFields<P, T>
{
    private PrivateFieldsClient(Class<P> owner, Obf obf)
    {
        super(owner, obf);
    }

    // CHECKSTYLE:OFF

    public static final PrivateFieldsClient<TileEntity, Map> tileEntityNameToClassMap = new PrivateFieldsClient<TileEntity, Map>(TileEntity.class, Obf.tileEntityNameToClassMap);
    public static final PrivateFieldsClient<TileEntity, Map> tileEntityClassToNameMap = new PrivateFieldsClient<TileEntity, Map>(TileEntity.class, Obf.tileEntityClassToNameMap);
}