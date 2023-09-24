package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.util.FunctionUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerTextureSupplier {
    public static <T extends LivingEntity> TextureSupplier<T> create(TextureSupplier<T> fallback) {
        Function<T, Entry> customNameCache = FunctionUtil.memoize(Entry::new, entity -> entity.getCustomName().getString() + "_" + entity.getUuidAsString());
        return entity -> {
            Identifier override = entity.hasCustomName() ? customNameCache.apply(entity).getTexture() : null;
            if (override != null) {
                return override;
            }
            return fallback.apply(entity);
        };
    }

    static final class Entry {
        private final CompletableFuture<Identifier> profile;

        Entry(LivingEntity entity) {
            profile = SkullBlockEntity.fetchProfile(entity.getCustomName().getString()).thenApply(profile -> {
                return profile
                        .map(p -> SkinsProxy.instance.getSkinTexture(p))
                        .filter(skin -> !IPony.getManager().getPony(skin).race().isHuman())
                        .orElse(null);
            });
        }

        @Nullable
        public Identifier getTexture() {
            return profile.getNow(null);
        }
    }
}
