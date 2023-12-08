package com.minelittlepony.client;

import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PonyManagerImpl implements PonyManager, SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = new Identifier("minelittlepony", "background_ponies");

    private final PonyConfig config;

    private final LoadingCache<Identifier, Pony> defaultedPoniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(resource -> new Pony(resource, PonyDataLoader.parse(resource, true))));

    private final LoadingCache<Identifier, Pony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(resource -> new Pony(resource, PonyDataLoader.parse(resource, false))));

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
    public Pony getPony(PlayerEntity player) {
        return getPony(getSkin(player), player instanceof ForcedPony ? null : player.getGameProfile() == null ? player.getUuid() : player.getGameProfile().getId());
    }

    @Override
    public Optional<Pony> getPony(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            return Optional.of(getPony(player));
        }
        Identifier skin = getSkin(entity);
        return skin == null ? Optional.empty() : Optional.of(getPony(skin, null));
    }

    @Override
    public Pony getPony(@Nullable Identifier resource, @Nullable UUID uuid) {
        if (resource == null) {
            return uuid == null ? loadPony(DefaultSkinHelper.getTexture(), true) : getBackgroundPony(uuid);
        }

        Pony pony = loadPony(resource, false);

        if (uuid != null && PonyConfig.getInstance().ponyLevel.get() == PonyLevel.PONIES && pony.metadata().race().isHuman()) {
            return getBackgroundPony(uuid);
        }
        return pony;
    }

    @Override
    public Pony getBackgroundPony(@Nullable UUID uuid) {
        if (config.ponyLevel.get() == PonyLevel.PONIES) {
            return loadPony(MineLittlePony.getInstance().getVariatedTextures().get(VariatedTextureSupplier.BACKGROUND_PONIES_POOL, uuid).orElse(DefaultSkinHelper.getSkinTextures(uuid).texture()), true);
        }
        return loadPony(DefaultSkinHelper.getSkinTextures(uuid).texture(), true);
    }

    @Nullable
    private Identifier getSkin(LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            if (player.getGameProfile() != null && player instanceof AbstractClientPlayerEntity clientPlayer) {
                return clientPlayer.getSkinTextures().texture();
            }
        } else {
            if (MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity) != null) {
                return MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTexture(entity);
            }
        }

        return null;
    }

    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
        defaultedPoniesCache.invalidate(resource);
    }

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
