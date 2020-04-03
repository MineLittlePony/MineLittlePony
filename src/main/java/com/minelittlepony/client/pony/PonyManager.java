package com.minelittlepony.client.pony;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.MoreStreams;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.minelittlepony.util.MathUtil;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    private static final Gson GSON = new Gson();

    /**
     * All currently loaded background ponies.
     */
    private List<Identifier> backgroundPonyList = Lists.newArrayList();

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

    public IPony getPony(PlayerListEntry playerInfo) {
        Identifier skin = playerInfo.getSkinTexture();
        UUID uuid = playerInfo.getProfile().getId();

        if (skin == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
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
        if (getNumberOfPonies() == 0 || isUser(uuid)) {
            return getPony(IPonyManager.getDefaultSkin(uuid));
        }

        int bgi = MathUtil.mod(uuid.hashCode(), getNumberOfPonies());

        return getPony(backgroundPonyList.get(bgi));
    }

    private boolean isUser(UUID uuid) {
        return MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getUuid().equals(uuid);
    }

    @Override
    public void removePony(Identifier resource) {
        poniesCache.invalidate(resource);
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer sync, ResourceManager sender,
            Profiler serverProfiler, Profiler clientProfiler,
            Executor serverExecutor, Executor clientExecutor) {

        sync.getClass();
        return sync.whenPrepared(null).thenRunAsync(() -> {
            clientProfiler.startTick();
            clientProfiler.push("Reloading all background ponies");
            reloadAll(sender);
            clientProfiler.pop();
            clientProfiler.endTick();
        }, clientExecutor);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public void clearCache() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
    }

    public void reloadAll(ResourceManager resourceManager) {
        poniesCache.invalidateAll();
        backgroundPonyList.clear();

        List<Identifier> collectedPaths = new LinkedList<>();
        List<BackgroundPonies> collectedPonies = new LinkedList<>();

        Queue<BackgroundPonies> processingQueue = new LinkedList<>();

        for (String domain : resourceManager.getAllNamespaces()) {
            processingQueue.addAll(loadBgPonies(resourceManager, new Identifier(domain, BGPONIES_JSON)));
        }

        BackgroundPonies item;
        while ((item = processingQueue.poll()) != null) {
            for (Identifier imp : item.getImports()) {
                if (!collectedPaths.contains(imp)) {
                    collectedPaths.add(imp);
                    processingQueue.addAll(loadBgPonies(resourceManager, imp));
                }
            }

            collectedPonies.add(item);
        }

        for (BackgroundPonies i : collectedPonies) {
            if (i.override) {
                backgroundPonyList.clear();
            }

            backgroundPonyList.addAll(i.getPonies());
        }

        backgroundPonyList = MoreStreams.distinct(backgroundPonyList);

        MineLittlePony.logger.info("Detected {} background ponies installed.", getNumberOfPonies());
    }

    private Queue<BackgroundPonies> loadBgPonies(ResourceManager resourceManager, Identifier location) {
        Queue<BackgroundPonies> collectedPonies = new LinkedList<>();

        try {
            String path = location.getPath().replace("bgponies.json", "");

            for (Resource res : resourceManager.getAllResources(location)) {
                try (Reader reader = new InputStreamReader((res.getInputStream()))) {
                    BackgroundPonies ponies = GSON.fromJson(reader, BackgroundPonies.class);

                    ponies.domain = location.getNamespace();
                    ponies.path = path;

                    collectedPonies.add(ponies);
                } catch (JsonParseException e) {
                    MineLittlePony.logger.error("Invalid bgponies.json in " + res.getResourcePackName(), e);
                }
            }
        } catch (IOException ignored) {
            // this isn't the exception you're looking for.
        }

        return collectedPonies;
    }

    private int getNumberOfPonies() {
        return backgroundPonyList.size();
    }

    private static class BackgroundPonies {

        private boolean override;

        private List<String> ponies;

        private List<String> imports = new ArrayList<>();

        private String domain;
        private String path;

        private Identifier apply(String input) {
            return new Identifier(domain, String.format("%s%s.png", path, input));
        }

        private Identifier makeImport(String input) {
            return new Identifier(domain, String.format("%s%s/bgponies.json", path, input));
        }

        public List<Identifier> getPonies() {
            return MoreStreams.map(ponies, this::apply);
        }

        public List<Identifier> getImports() {
            return MoreStreams.map(imports, this::makeImport);
        }
    }

    public void onSkinCacheCleared() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.invalidateAll();
    }
}
