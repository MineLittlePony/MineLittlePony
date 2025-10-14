package com.minelittlepony.client.mixin;

import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtComponent.class)
public interface NbtAccessor {
    @Accessor("nbt")
    NbtCompound minelp_getNbt();
}
