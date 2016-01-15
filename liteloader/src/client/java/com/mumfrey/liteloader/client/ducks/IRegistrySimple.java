package com.mumfrey.liteloader.client.ducks;

import java.util.Map;

public interface IRegistrySimple
{
    public abstract <K, V> Map<K, V> getRegistryObjects();
}
