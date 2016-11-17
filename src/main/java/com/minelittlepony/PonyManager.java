package com.minelittlepony;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.compress.utils.IOUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.util.MineLPLogger;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class PonyManager implements IResourceManagerReloadListener {

    public static final ResourceLocation STEVE = new ResourceLocation("minelittlepony", "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation("minelittlepony", "textures/entity/alex_pony.png");

    private static final ResourceLocation BGPONIES_JSON = new ResourceLocation("minelittlepony", "textures/entity/pony/bgponies.json");
    private List<ResourceLocation> backgroundPonyList = Lists.newArrayList();

    private PonyConfig config;

    private Map<ResourceLocation, Pony> poniesCache = Maps.newHashMap();
    private Map<ResourceLocation, Pony> backgroudPoniesCache = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
        initmodels();
    }

    public void initmodels() {
        MineLPLogger.info("Initializing models...");
        PMAPI.init();
        MineLPLogger.info("Done initializing models.");
    }

    private Pony getPonyFromResourceRegistry(ResourceLocation skinResourceLocation, AbstractClientPlayer player) {
        Pony myLittlePony;
        if (!this.poniesCache.containsKey(skinResourceLocation)) {
            if (player != null) {
                myLittlePony = new Pony(player);
            } else {
                myLittlePony = new Pony(skinResourceLocation);
            }

            this.poniesCache.put(skinResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.poniesCache.get(skinResourceLocation);
        }

        return myLittlePony;
    }

    public Pony getPonyFromResourceRegistry(ResourceLocation skinResourceLocation) {
        return this.getPonyFromResourceRegistry(skinResourceLocation, (AbstractClientPlayer) null);
    }

    public Pony getPonyFromResourceRegistry(AbstractClientPlayer player) {
        Pony myLittlePony = this.getPonyFromResourceRegistry(player.getLocationSkin(), player);
        if (config.getPonyLevel() == PonyLevel.PONIES && myLittlePony.metadata.getRace() == null) {
            myLittlePony = this.getPonyFromBackgroundResourceRegistry(player);
        }

        return myLittlePony;
    }

    public ResourceLocation getBackgroundPonyResource(UUID id) {
        if (getNumberOfPonies() > 0) {
            int backgroundIndex = id.hashCode() % this.getNumberOfPonies();
            if (backgroundIndex < 0) {
                backgroundIndex += this.getNumberOfPonies();
            }

            return backgroundPonyList.get(backgroundIndex);
        }
        return STEVE;
    }

    public Pony getPonyFromBackgroundResourceRegistry(AbstractClientPlayer player) {
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
        // TODO Auto-generated method stub
        this.backgroudPoniesCache.clear();
        this.backgroundPonyList.clear();
        try {
            for (IResource res : resourceManager.getAllResources(BGPONIES_JSON)) {
                try {
                    BackgroundPonies ponies = getBackgroundPonies(res.getInputStream());
                    if (ponies.override) {
                        this.backgroundPonyList.clear();
                    }
                    this.backgroundPonyList.addAll(ponies.getPonies());
                } catch (JsonParseException e) {
                    MineLPLogger.error(e, "Invalid bgponies.json in {}", res.getResourcePackName());
                }
            }
        } catch (IOException e) {
            // this isn't the exception you're looking for.
        }
        MineLPLogger.info("Detected %d background ponies installed.", getNumberOfPonies());
    }

    private BackgroundPonies getBackgroundPonies(InputStream stream) {
        try {
            return new Gson().fromJson(new InputStreamReader(stream), BackgroundPonies.class);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private ResourceLocation getDefaultSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 0 ? STEVE : ALEX;
    }

    private int getNumberOfPonies() {
        return backgroundPonyList.size();
    }

    private static class BackgroundPonies implements Function<String, ResourceLocation> {

        public boolean override;
        private List<String> ponies;

        @Override
        public ResourceLocation apply(String input) {
            return new ResourceLocation("minelittlepony", String.format("textures/entity/pony/%s.png", input));
        }

        public List<ResourceLocation> getPonies() {
            return Lists.transform(ponies, this);
        }
    }
}
