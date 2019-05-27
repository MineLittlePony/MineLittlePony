package com.minelittlepony.client.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface IResizeable {
    @Accessor("size")
    EntitySize getCurrentSize();

    @Accessor("size")
    void setCurrentSize(EntitySize size);
}
