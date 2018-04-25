package com.minelittlepony;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.ducks.IPlayerInfo;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyLevel;

import net.minecraft.client.entity.AbstractClientPlayer;
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

public class PonyManager implements IResourceManagerReloadListener {

    public static final ResourceLocation STEVE = new ResourceLocation("minelittlepony", "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation("minelittlepony", "textures/entity/alex_pony.png");

    private static final ResourceLocation BGPONIES_JSON = new ResourceLocation("minelittlepony", "textures/entity/pony/bgponies.json");

    private static final Gson GSON = new Gson();

    private List<ResourceLocation> backgroundPonyList = Lists.newArrayList();

    private PonyConfig config;

    private Map<ResourceLocation, Pony>
        poniesCache = Maps.newHashMap(),
        backgroudPoniesCache = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
        initmodels();
    }

    public void initmodels() {
        MineLittlePony.logger.info("Initializing models...");
        PMAPI.init();
        MineLittlePony.logger.info("Done initializing models.");
    }

    public Pony getPony(ResourceLocation skinResourceLocation, boolean slim) {
        return poniesCache.computeIfAbsent(skinResourceLocation, res -> new Pony(res, slim));
    }

    public Pony getPony(AbstractClientPlayer player) {
        Pony pony = getPony(player.getLocationSkin(), IPlayerInfo.getPlayerInfo(player).usesSlimArms());

        if (config.getPonyLevel() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(player.getUniqueID());
        }

        return pony;
    }
    
    public Pony getPony(ResourceLocation resource, UUID uuid) {
        Pony pony = getPony(resource, isSlimSkin(uuid));
        
        if (config.getPonyLevel() == PonyLevel.PONIES && pony.getMetadata().getRace().isHuman()) {
            return getBackgroundPony(uuid);
        }
        
        return pony;
    }

    public Pony getDefaultPony(UUID uuid) {
        if (config.getPonyLevel() != PonyLevel.PONIES) {
            return getPony(DefaultPlayerSkin.getDefaultSkin(uuid), isSlimSkin(uuid));
        }

        return getBackgroundPony(uuid);
    }

    private Pony getBackgroundPony(UUID uuid) {
        if (getNumberOfPonies() == 0) return getPony(getDefaultSkin(uuid), isSlimSkin(uuid));

        int bgi = uuid.hashCode() % this.getNumberOfPonies();
        while (bgi < 0) bgi += this.getNumberOfPonies();

        return getPony(backgroundPonyList.get(bgi), false);
    }

    public Pony removePony(ResourceLocation location) {
        return poniesCache.remove(location);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.poniesCache.clear();
        this.backgroudPoniesCache.clear();
        this.backgroundPonyList.clear();
        try {
            for (IResource res : resourceManager.getAllResources(BGPONIES_JSON)) {
                try (Reader reader = new InputStreamReader((res.getInputStream()))) {
                    BackgroundPonies ponies = GSON.fromJson(reader, BackgroundPonies.class);
                    if (ponies.override) {
                        this.backgroundPonyList.clear();
                    }
                    this.backgroundPonyList.addAll(ponies.getPonies());
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
            return this.ponies.stream().map(this::apply).collect(Collectors.toList());
        }
    }
}
