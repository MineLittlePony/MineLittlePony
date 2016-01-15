package com.mumfrey.liteloader.client.ducks;

import java.util.IdentityHashMap;
import java.util.List;

public interface IObjectIntIdentityMap
{
    public abstract <V> IdentityHashMap<V, Integer> getIdentityMap();

    public abstract <V> List<V> getObjectList();
}
