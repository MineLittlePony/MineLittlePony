package com.minelittlepony.client.render.entity.npc.textures;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.pony.PonyManager;
import com.mojang.authlib.GameProfile;

import java.util.*;

public class CustomPonyTextures<T extends LivingEntity> implements TextureSupplier<T> {

    private final TextureSupplier<T> fallback;
    private final Map<String, Entry> customNameCache = new HashMap<>();

    public CustomPonyTextures(TextureSupplier<T> fallback) {
        this.fallback = fallback;
    }

    @Override
    public Identifier supplyTexture(T entity) {
        Identifier override = getCustomTexture(entity);
        if (override != null) {
            return override;
        }
        return fallback.supplyTexture(entity);
    }

    @Nullable
    private Identifier getCustomTexture(T entity) {
        if (!entity.hasCustomName()) {
            return null;
        }

        String key = entity.getCustomName().getString() + "_" + entity.getUuidAsString();

        if (!customNameCache.containsKey(key)) {
            customNameCache.put(key, new Entry(entity));
        }
        return customNameCache.get(key).getTexture();
    }

    class Entry {
        private final UUID uuid;
        private final Identifier texture;

        @Nullable
        private GameProfile profile;

        Entry(T entity) {
            uuid = entity.getUuid();
            texture = MineLittlePony.getInstance().getVariatedTextures()
                    .get(PonyManager.BACKGROUND_PONIES)
                    .getByName(entity.getCustomName().getString(), uuid)
                    .orElse(null);

            if (texture == null) {
                SkullBlockEntity.loadProperties(new GameProfile(null, entity.getCustomName().getString()), resolved -> {
                    profile = resolved;
                });
            }
        }

        public Identifier getTexture() {
            if (profile != null) {
                Identifier skin = SkinsProxy.instance.getSkinTexture(profile);
                if (skin != null) {
                    if (IPony.getManager().getPony(skin).race().isHuman()) {
                        if (PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES) {
                            return IPony.getManager().getBackgroundPony(uuid).texture();
                        }
                    }
                    return skin;
                }
            }
            return texture;
        }
    }
}
