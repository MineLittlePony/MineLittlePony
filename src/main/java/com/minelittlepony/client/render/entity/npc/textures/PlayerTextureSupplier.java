package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.SkinsProxy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerTextureSupplier {
    public static <T extends LivingEntity> TextureSupplier<T> create(TextureSupplier<T> fallback) {
        Function<String, Entry> customNameCache = Util.memoize(Entry::new);
        return entity -> {
            Identifier override = entity.hasCustomName() ? customNameCache.apply(entity.getCustomName().getString()).getTexture() : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }

    static final class Entry {
        private final CompletableFuture<Identifier> profile;

        Entry(String name) {
            profile = SkullBlockEntity.fetchProfile(name).thenApply(profile -> {
                return profile
                        .map(p -> SkinsProxy.instance.getSkinTexture(p))
                        .filter(skin -> !Pony.getManager().getPony(skin).race().isHuman())
                        .orElse(null);
            });
        }

        @Nullable
        public Identifier getTexture() {
            return profile.getNow(null);
        }
    }
}
