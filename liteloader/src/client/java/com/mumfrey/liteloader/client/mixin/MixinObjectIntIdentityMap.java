package com.mumfrey.liteloader.client.mixin;

import java.util.IdentityHashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.client.ducks.IObjectIntIdentityMap;

import net.minecraft.util.ObjectIntIdentityMap;

@Mixin(ObjectIntIdentityMap.class)
public abstract class MixinObjectIntIdentityMap implements IObjectIntIdentityMap
{
    @Shadow private IdentityHashMap<?, Integer> identityMap;
    @Shadow private List<?> objectList;
    
    @SuppressWarnings("unchecked")
    @Override
    public <V> IdentityHashMap<V, Integer> getIdentityMap()
    {
        return (IdentityHashMap<V, Integer>)this.identityMap;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <V> List<V> getObjectList()
    {
        return (List<V>)this.objectList;
    }
}
