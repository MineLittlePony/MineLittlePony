package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.client.MineLittlePony;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A texture pool for generating multiple associated textures.
 */
@FunctionalInterface
public interface TextureSupplier<T> extends Function<T, Identifier> {
    /**
     * Supplies a new texture. May be generated for returned from a pool indexed by the given key.
     */
    @Override
    Identifier apply(T key);

    static TextureSupplier<String> formatted(String domain, String path) {
        return key -> new Identifier(domain, String.format(path, key));
    }

    static <T extends LivingEntity> TextureSupplier<T> ofVariations(Identifier poolId, TextureSupplier<T> fallback) {
        return entity -> {
            return MineLittlePony.getInstance().getVariatedTextures().get(poolId).getId(entity.getUuid()).orElse(fallback.apply(entity));
        };
    }

    static <T extends LivingEntity> TextureSupplier<T> ofPool(Identifier poolId, TextureSupplier<T> fallback) {
        final BiFunction<String, UUID, Identifier> cache = Util.memoize((name, uuid) -> {
            return MineLittlePony.getInstance().getVariatedTextures()
                    .get(poolId)
                    .getByName(name, uuid)
                    .orElse(null);
        });
        return entity -> {
            Identifier override = entity.hasCustomName() ? cache.apply(entity.getCustomName().getString(), entity.getUuid()) : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }

    static <A> TextureSupplier<A> of(Identifier texture) {
        return a -> texture;
    }

    static <A> TextureSupplier<A> memoize(Function<A, Identifier> func, Function<A, String> keyFunc) {
        final Map<String, Identifier> cache = new ConcurrentHashMap<>();
        return a -> cache.computeIfAbsent(keyFunc.apply(a), k -> func.apply(a));
    }
}
