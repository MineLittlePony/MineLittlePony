package com.mumfrey.liteloader.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.client.ducks.IRegistrySimple;

import net.minecraft.util.RegistrySimple;

@Mixin(RegistrySimple.class)
public abstract class MixinRegistrySimple implements IRegistrySimple
{
    @Shadow protected Map<?, ?> registryObjects;
    
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Map<K, V> getRegistryObjects()
    {
        return (Map<K, V>)this.registryObjects;
    }
}
