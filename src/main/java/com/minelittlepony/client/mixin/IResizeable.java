package com.minelittlepony.client.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface IResizeable {
    @Accessor("dimensions")
    EntityDimensions getCurrentSize();

    @Accessor("dimensions")
    void setCurrentSize(EntityDimensions size);
}
