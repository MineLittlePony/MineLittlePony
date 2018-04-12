package com.minelittlepony;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.model.PMAPI;
import net.minecraft.client.entity.AbstractClientPlayer;
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

    private Map<ResourceLocation, Pony> poniesCache = Maps.newHashMap();
    private Map<ResourceLocation, Pony> backgroudPoniesCache = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
        initmodels();
    }

    public void initmodels() {
        MineLittlePony.logger.info("Initializing models...");
        PMAPI.init();
        MineLittlePony.logger.info("Done initializing models.");
    }

    public Pony getPony(ResourceLocation skinResourceLocation) {
        return this.poniesCache.computeIfAbsent(skinResourceLocation, Pony::new);
    }

    public Pony getPony(AbstractClientPlayer player) {

        Pony myLittlePony = this.poniesCache.computeIfAbsent(player.getLocationSkin(), res -> new Pony(player));

        if (config.getPonyLevel() == PonyLevel.PONIES && myLittlePony.getMetadata().getRace() == PonyRace.HUMAN) {
            myLittlePony = this.getPonyFromBackgroundResourceRegistry(player);
        }

        return myLittlePony;
    }

    public Pony removePony(ResourceLocation location) {
        return this.poniesCache.remove(location);
    }

    private ResourceLocation getBackgroundPonyResource(UUID id) {
        if (getNumberOfPonies() > 0) {
            int backgroundIndex = id.hashCode() % this.getNumberOfPonies();
            if (backgroundIndex < 0) {
                backgroundIndex += this.getNumberOfPonies();
            }

            return backgroundPonyList.get(backgroundIndex);
        }
        return STEVE;
    }

    private Pony getPonyFromBackgroundResourceRegistry(AbstractClientPlayer player) {
        ResourceLocation textureResourceLocation;
        if (player.isUser()) {
            textureResourceLocation = getDefaultSkin(player.getUniqueID());
        } else {
            textureResourceLocation = this.getBackgroundPonyResource(player.getUniqueID());
        }

        Pony myLittlePony;
        if (!this.backgroudPoniesCache.containsKey(textureResourceLocation)) {
            myLittlePony = new Pony(textureResourceLocation);
            this.backgroudPoniesCache.put(textureResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.backgroudPoniesCache.get(textureResourceLocation);
        }

        return myLittlePony;
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
        return (uuid.hashCode() & 1) == 0 ? STEVE : ALEX;
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
