package com.minelittlepony;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyLevel;
import com.voxelmodpack.hdskins.ISkinCacheClearListener;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public class PonyManager implements IResourceManagerReloadListener, ISkinCacheClearListener {

    public static final ResourceLocation STEVE = new ResourceLocation("minelittlepony", "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation("minelittlepony", "textures/entity/alex_pony.png");
    public static final ResourceLocation BGPONIES_JSON = new ResourceLocation("minelittlepony", "textures/entity/pony/bgponies.json");

    private static final Gson GSON = new Gson();

    /**
     * All currently loaded background ponies.
     */
    private List<ResourceLocation> backgroundPonyList = Lists.newArrayList();

    private PonyConfig config;

    private Map<ResourceLocation, Pony> poniesCache = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
    }

    /**
     * Gets or creates a pony for the given skin resource and vanilla model type.
     *
     * @param resource A texture resource
     */
    public Pony getPony(ResourceLocation resource, boolean slim) {
        Pony pony = poniesCache.computeIfAbsent(resource, res -> new Pony(res, slim));

        if (pony.usesThinArms() != slim) {
            pony = new Pony(resource, slim);
            poniesCache.put(resource,  pony);
        }

        return pony;
    }

    /**
     * Gets or creates a pony for the given player.
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param player the player
     */
    public Pony getPony(AbstractClientPlayer player) {
        ResourceLocation skin = player.getLocationSkin();
        UUID uuid = player.getGameProfile().getId();

        if (skin == null) return getDefaultPony(uuid);

        return getPony(skin, uuid);
    }

    public Pony getPony(NetworkPlayerInfo playerInfo) {
        // force load HDSkins if they're not available

        ResourceLocation skin = playerInfo.getLocationSkin();
        UUID uuid = playerInfo.getGameProfile().getId();

        if (skin == null) return getDefaultPony(uuid);

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
    public Pony getPony(ResourceLocation resource, UUID uuid) {
        Pony pony = getPony(resource, isSlimSkin(uuid));

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
    public Pony getDefaultPony(UUID uuid) {
        if (config.getPonyLevel() != PonyLevel.PONIES) {
            return getPony(DefaultPlayerSkin.getDefaultSkin(uuid), isSlimSkin(uuid));
        }

        return getBackgroundPony(uuid);
    }

    private Pony getBackgroundPony(UUID uuid) {
        if (getNumberOfPonies() == 0 || isUser(uuid)) {
            return getPony(getDefaultSkin(uuid), isSlimSkin(uuid));
        }

        int bgi = uuid.hashCode() % getNumberOfPonies();
        while (bgi < 0) bgi += getNumberOfPonies();

        return getPony(backgroundPonyList.get(bgi), false);
    }

    private boolean isUser(UUID uuid) {
        return Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getUniqueID().equals(uuid);
    }

    /**
     * De-registers a pony from the cache.
     */
    public Pony removePony(ResourceLocation resource) {
        return poniesCache.remove(resource);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        poniesCache.clear();
        backgroundPonyList.clear();
        try {
            for (IResource res : resourceManager.getAllResources(BGPONIES_JSON)) {
                try (Reader reader = new InputStreamReader((res.getInputStream()))) {
                    BackgroundPonies ponies = GSON.fromJson(reader, BackgroundPonies.class);
                    if (ponies.override) {
                        backgroundPonyList.clear();
                    }
                    backgroundPonyList.addAll(ponies.getPonies());
                } catch (JsonParseException e) {
                    MineLittlePony.logger.error("Invalid bgponies.json in " + res.getResourcePackName(), e);
                }
            }
        } catch (IOException ignored) {
            // this isn't the exception you're looking for.
        }
        MineLittlePony.logger.info("Detected {} background ponies installed.", getNumberOfPonies());
    }

    private ResourceLocation getDefaultSkin(UUID uuid) {
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

        private BackgroundPonies(List<String> ponies, boolean override) {
            this.ponies = ponies;
            this.override = override;
        }

        private ResourceLocation apply(String input) {
            return new ResourceLocation("minelittlepony", String.format("textures/entity/pony/%s.png", input));
        }

        public List<ResourceLocation> getPonies() {
            return ponies.stream().map(this::apply).collect(Collectors.toList());
        }
    }

    @Override
    public boolean onSkinCacheCleared() {
        MineLittlePony.logger.info("Flushed {} cached ponies.", poniesCache.size());
        poniesCache.clear();
        return true;
    }
}
