package com.minelittlepony.client.pony;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.common.util.MoreStreams;
import com.minelittlepony.hdskins.ISkinCacheClearListener;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.IPonyManager;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.minelittlepony.util.chron.ChronicCache;
import com.minelittlepony.util.math.MathUtil;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public class PonyManager implements IPonyManager, IResourceManagerReloadListener, ISkinCacheClearListener {

    private static final Gson GSON = new Gson();

    /**
     * All currently loaded background ponies.
     */
    private List<ResourceLocation> backgroundPonyList = Lists.newArrayList();

    private final PonyConfig config;

    private final ChronicCache<ResourceLocation, Pony> poniesCache = new ChronicCache<>();

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    @Override
    public IPony getPony(ResourceLocation resource) {
        return poniesCache.retrieve(resource, Pony::new);
    }

    @Override
    public IPony getPony(EntityPlayer player) {
        ResourceLocation skin = getSkin(player);
        UUID uuid = player.getGameProfile().getId();

        if (Pony.getBufferedImage(skin) == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
    }

    @Nullable
    ResourceLocation getSkin(EntityPlayer player) {
        if (player instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer)player).getLocationSkin();
        }

        return null;
    }

    public IPony getPony(NetworkPlayerInfo playerInfo) {
        ResourceLocation skin = playerInfo.getLocationSkin();
        UUID uuid = playerInfo.getGameProfile().getId();

        if (Pony.getBufferedImage(skin) == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
    }

    @Override
    public IPony getPony(ResourceLocation resource, UUID uuid) {
        IPony pony = getPony(resource);

        if (config.getPonyLevel() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    @Override
    public IPony getDefaultPony(UUID uuid) {
        if (config.getPonyLevel() != PonyLevel.PONIES) {
            return getPony(DefaultPlayerSkin.getDefaultSkin(uuid));
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
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUniqueID().equals(uuid);
    }

    @Override
    public IPony removePony(ResourceLocation resource) {
        return poniesCache.remove(resource);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        poniesCache.clear();
        backgroundPonyList.clear();

        List<ResourceLocation> collectedPaths = new LinkedList<>();
        List<BackgroundPonies> collectedPonies = new LinkedList<>();

        Queue<BackgroundPonies> processingQueue = new LinkedList<>();

        for (String domain : resourceManager.getResourceNamespaces()) {
            processingQueue.addAll(loadBgPonies(resourceManager, new ResourceLocation(domain, BGPONIES_JSON)));
        }

        BackgroundPonies item;
        while ((item = processingQueue.poll()) != null) {
            for (ResourceLocation imp : item.getImports()) {
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

    private Queue<BackgroundPonies> loadBgPonies(IResourceManager resourceManager, ResourceLocation location) {
        Queue<BackgroundPonies> collectedPonies = new LinkedList<>();

        try {
            String path = location.getPath().replace("bgponies.json", "");

            for (IResource res : resourceManager.getAllResources(location)) {
                try (Reader reader = new InputStreamReader((res.getInputStream()))) {
                    BackgroundPonies ponies = GSON.fromJson(reader, BackgroundPonies.class);

                    ponies.domain = location.getNamespace();
                    ponies.path = path;

                    collectedPonies.add(ponies);
                } catch (JsonParseException e) {
                    MineLittlePony.logger.error("Invalid bgponies.json in " + res.getPackName(), e);
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

        private ResourceLocation apply(String input) {
            return new ResourceLocation(domain, String.format("%s%s.png", path, input));
        }

        private ResourceLocation makeImport(String input) {
            return new ResourceLocation(domain, String.format("%s%s/bgponies.json", path, input));
        }

        public List<ResourceLocation> getPonies() {
            return MoreStreams.map(ponies, this::apply);
        }

        public List<ResourceLocation> getImports() {
            return MoreStreams.map(imports, this::makeImport);
        }
    }

    @Override
    public boolean onSkinCacheCleared() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.clear();
        return true;
    }
}
