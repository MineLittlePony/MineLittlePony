package com.minelittlepony;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.util.math.MathUtil;
import com.voxelmodpack.hdskins.ISkinCacheClearListener;
import com.voxelmodpack.hdskins.util.MoreStreams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public class PonyManager implements IResourceManagerReloadListener, ISkinCacheClearListener {

    public static final ResourceLocation STEVE = new ResourceLocation("minelittlepony", "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation("minelittlepony", "textures/entity/alex_pony.png");

    public static final String BGPONIES_JSON = "textures/entity/pony/bgponies.json";

    private static final Gson GSON = new Gson();

    /**
     * All currently loaded background ponies.
     */
    private List<ResourceLocation> backgroundPonyList = Lists.newArrayList();

    private PonyConfig config;

    private Map<ResourceLocation, IPony> poniesCache = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    /**
     * Gets or creates a pony for the given skin resource and vanilla model type.
     *
     * @param resource A texture resource
     */
    public IPony getPony(ResourceLocation resource) {
        return poniesCache.computeIfAbsent(resource, Pony::new);
    }

    /**
     * Gets or creates a pony for the given player.
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param player the player
     */
    public IPony getPony(AbstractClientPlayer player) {
        ResourceLocation skin = player.getLocationSkin();
        UUID uuid = player.getGameProfile().getId();

        if (Pony.getBufferedImage(skin) == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
    }

    public IPony getPony(NetworkPlayerInfo playerInfo) {
        ResourceLocation skin = playerInfo.getLocationSkin();
        UUID uuid = playerInfo.getGameProfile().getId();

        if (Pony.getBufferedImage(skin) == null) {
            return getDefaultPony(uuid);
        }

        return getPony(skin, uuid);
    }

    /**
     * Gets or creates a pony for the given skin resource and entity id.
     *
     * Whether is has slim arms is determined by the id.
     *
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param resource A texture resource
     * @param uuid id of a player or entity
     */
    public IPony getPony(ResourceLocation resource, UUID uuid) {
        IPony pony = getPony(resource);

        if (config.getPonyLevel() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }

        return pony;
    }

    /**
     * Gets the default pony. Either STEVE/ALEX, or a background pony based on client settings.
     *
     * @param uuid id of a player or entity
     */
    public IPony getDefaultPony(UUID uuid) {
        if (config.getPonyLevel() != PonyLevel.PONIES) {
            return getPony(DefaultPlayerSkin.getDefaultSkin(uuid));
        }

        return getBackgroundPony(uuid);
    }

    /**
     * Gets a random background pony determined by the given uuid.
     *
     * Useful for mods that offer customisation, especially ones that have a whole lot of NPCs.
     *
     * @param uuid  A UUID. Either a user or an entity.
     */
    public IPony getBackgroundPony(UUID uuid) {
        if (getNumberOfPonies() == 0 || isUser(uuid)) {
            return getPony(getDefaultSkin(uuid));
        }

        int bgi = MathUtil.mod(uuid.hashCode(), getNumberOfPonies());

        return getPony(backgroundPonyList.get(bgi));
    }

    private boolean isUser(UUID uuid) {
        return Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getUniqueID().equals(uuid);
    }

    /**
     * De-registers a pony from the cache.
     */
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

        for (String domain : resourceManager.getResourceDomains()) {
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
                    MineLittlePony.logger.error("Invalid bgponies.json in " + res.getResourcePackName(), e);
                }
            }
        } catch (IOException ignored) {
            // this isn't the exception you're looking for.
        }

        return collectedPonies;
    }

    public static ResourceLocation getDefaultSkin(UUID uuid) {
        return isSlimSkin(uuid) ? ALEX : STEVE;
    }

    /**
     * Returns true if the given uuid is of a player would would use the ALEX skin type.
     */
    public static boolean isSlimSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
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
            return new CasedResourceLocation(domain, String.format("%s%s.png", path, input));
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
