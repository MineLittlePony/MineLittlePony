package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.util.Identifier;

/**
 * A texture pool for generating multiple associated textures.
 */
@FunctionalInterface
public interface TextureSupplier<T> {
    /**
     * Supplies a new texture. May be generated for returned from a pool indexed by the given key.
     */
    Identifier supplyTexture(T key);

    static TextureSupplier<String> formatted(String domain, String path) {
        return key -> new Identifier(domain, String.format(path, key));
    }
}
