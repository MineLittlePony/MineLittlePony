package com.minelittlepony.client;

import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 */
class PonyManagerImpl implements PonyManager, SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = new Identifier("minelittlepony", "background_ponies");

    private final PonyConfig config;

    private final LoadingCache<Identifier, Pony> defaultedPoniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(resource -> {
                return new Pony(resource, PonyDataLoader.parse(resource, true));
            }));

    private final LoadingCache<Identifier, Pony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(resource -> {
                return new Pony(resource, PonyDataLoader.parse(resource, false));
            }));

    public PonyManagerImpl(PonyConfig config) {
        this.config = config;
        Instance.instance = this;
    }

    private Pony loadPony(Identifier resource, boolean defaulted) {
        try {
            return (defaulted ? defaultedPoniesCache : poniesCache).get(resource);
        } catch (ExecutionException e) {
            return new Pony(resource, PonyDataLoader.NULL);
        }
    }

    @Override
    public Pony getPony(Identifier resource) {
        return loadPony(resource, false);
    }

    @Override
    public Optional<Pony> getPony(@Nullable Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return Optional.of(getPony(player));
        }

        if (entity instanceof LivingEntity living) {
            return Optional.ofNullable(MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(living)).map(d -> d.getEntityPony(living));
        }

        return Optional.empty();
    }

    @Override
    public Pony getPony(PlayerEntity player) {
        Identifier skin = getSkin(player);
        UUID uuid = player.getGameProfile() == null ? player.getUuid() : player.getGameProfile().getId();

        if (skin != null) {
            if (player instanceof PonyManager.ForcedPony) {
                return getPony(skin);
            }

            return getPony(skin, uuid);
        }

        if (config.ponyLevel.get() == PonyLevel.PONIES) {
            return getBackgroundPony(uuid);
        }

        return loadPony(DefaultSkinHelper.getTexture(uuid).texture(), true);
    }

    @Override
    public Pony getPony(Identifier resource, UUID uuid) {
        Pony pony = getPony(resource);

        if (config.ponyLevel.get() == PonyLevel.PONIES && pony.metadata().race().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    @Override
    public Pony getBackgroundPony(UUID uuid) {
        return loadPony(MineLittlePony.getInstance().getVariatedTextures().get(VariatedTextureSupplier.BACKGROUND_PONIES_POOL, uuid).orElse(DefaultSkinHelper.getTexture(uuid).texture()), true);
    }

    @Nullable
    private Identifier getSkin(PlayerEntity player) {
        if (player.getGameProfile() == null) {
            return null;
        }
        if (player instanceof AbstractClientPlayerEntity) {
            return ((AbstractClientPlayerEntity)player).method_52814().texture();
        }

        return null;
    }

    @Override
    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
        defaultedPoniesCache.invalidate(resource);
    }

    @Override
    public void clearCache() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
        defaultedPoniesCache.invalidateAll();
    }

    @Override
    public void reload(ResourceManager var1) {
        clearCache();
        PonySkullRenderer.reload();
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
