package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.client.ducks.INamespacedRegistry;
import com.mumfrey.liteloader.client.ducks.IObjectIntIdentityMap;

import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;

@Mixin(RegistryNamespaced.class)
public abstract class MixinRegistryNamespaced extends RegistrySimple implements INamespacedRegistry
{
    @Shadow protected ObjectIntIdentityMap underlyingIntegerMap;
    
    @Override
    public IObjectIntIdentityMap getUnderlyingMap()
    {
        return (IObjectIntIdentityMap)this.underlyingIntegerMap;
    }
}
