package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.util.FunctionUtil;
import com.mojang.authlib.GameProfile;

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
        @Nullable
        private GameProfile profile;

        Entry(LivingEntity entity) {
            SkullBlockEntity.loadProperties(new GameProfile(null, entity.getCustomName().getString()), resolved -> {
                profile = resolved;
            });
        }

        @Nullable
        public Identifier getTexture() {
            if (profile != null) {
                Identifier skin = SkinsProxy.instance.getSkinTexture(profile);
                if (skin != null && !IPony.getManager().getPony(skin).race().isHuman()) {
                    return skin;
                }
            }
            return null;
        }
    }
}
