package com.minelittlepony.client.pony;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import javax.annotation.Nullable;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public class PonyManager implements IPonyManager, IdentifiableResourceReloadListener {

    private static final Identifier ID = new Identifier("minelittlepony", "background_ponies");

    private final BackgroundPonyList backgroundPonyList = new BackgroundPonyList();

    private final PonyConfig config;

    private final LoadingCache<Identifier, IPony> poniesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(Pony::new));

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    @Override
    public IPony getPony(Identifier resource) {
        try {
            return poniesCache.get(resource);
        } catch (ExecutionException e) {
            return new Pony(resource, PonyData.NULL);
        }
    }

    @Override
    public IPony getPony(PlayerEntity player) {
        if (player.getGameProfile() == null) {
            return getDefaultPony(player.getUuid());
        }

        Identifier skin = getSkin(player);
        UUID uuid = player.getGameProfile().getId();

        if (skin == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
    }

    @Nullable
    private Identifier getSkin(PlayerEntity player) {
        if (player instanceof AbstractClientPlayerEntity) {
            return ((AbstractClientPlayerEntity)player).getSkinTexture();
        }

        return null;
    }

    @Override
    public IPony getPony(Identifier resource, UUID uuid) {
        IPony pony = getPony(resource);

        if (config.ponyLevel.get() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    @Override
    public IPony getDefaultPony(UUID uuid) {
        if (config.ponyLevel.get() != PonyLevel.PONIES) {
            return getPony(DefaultSkinHelper.getTexture(uuid));
        }

        return getBackgroundPony(uuid);
    }

    @Override
    public IPony getBackgroundPony(UUID uuid) {
        return getPony(backgroundPonyList.getId(uuid));
    }

    @Override
    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
    }

    public void clearCache() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer sync, ResourceManager sender,
            Profiler serverProfiler, Profiler clientProfiler,
            Executor serverExecutor, Executor clientExecutor) {

        sync.getClass();
        return sync.whenPrepared(null).thenRunAsync(() -> {
            clientProfiler.startTick();
            clientProfiler.push("Reloading all background ponies");
            poniesCache.invalidateAll();
            backgroundPonyList.reloadAll(sender);
            clientProfiler.pop();
            clientProfiler.endTick();
        }, clientExecutor);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
