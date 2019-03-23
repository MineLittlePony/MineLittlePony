package com.minelittlepony.util.resources;

import net.minecraft.util.ResourceLocation;

/**
 * A texture pool for generating multiple associated textures.
 */
@FunctionalInterface
public interface ITextureSupplier<T> {
    /**
     * Supplies a new texture. May be generated for returned from a pool indexed by the given key.
     */
    ResourceLocation supplyTexture(T key);
}
