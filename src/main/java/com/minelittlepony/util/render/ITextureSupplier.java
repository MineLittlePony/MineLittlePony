package com.minelittlepony.util.render;

import net.minecraft.util.ResourceLocation;

/**
 * A texture pool for generating multiple associated textures.
 */
@FunctionalInterface
public interface ITextureSupplier<T> {
    ResourceLocation supplyTexture(T key);
}
