package com.brohoof.minelittlepony;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.compress.utils.IOUtils;

import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class PonyManager implements IResourceManagerReloadListener {

    public static final String NAMESPACE = "minelittlepony";
    public static final ResourceLocation ZOMBIE = new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_pony.png");
    public static final ResourceLocation ZOMBIE_VILLAGER = new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_villager_pony.png");

    public static final Map<ZombieType, ResourceLocation> ZOMBIES = Maps.immutableEnumMap(ImmutableMap.<ZombieType, ResourceLocation> builder()
            .put(ZombieType.NORMAL, ZOMBIE)
            .put(ZombieType.HUSK, new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_husk_pony.png"))
            .put(ZombieType.VILLAGER_FARMER, new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_farmer_pony.png"))
            .put(ZombieType.VILLAGER_LIBRARIAN, new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_librarian_pony.png"))
            .put(ZombieType.VILLAGER_PRIEST, new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_priest_pony.png"))
            .put(ZombieType.VILLAGER_SMITH, new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_smith_pony.png"))
            .put(ZombieType.VILLAGER_BUTCHER, new ResourceLocation(NAMESPACE, "textures/entity/zombie_villager/zombie_butcher_pony.png"))
            .build());

    public static final List<ResourceLocation> VILLAGER_LIST = ImmutableList.<ResourceLocation> builder()
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/farmer_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/librarian_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/priest_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/smith_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/butcher_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/villager_pony.png"))
            .build();

    public static final ResourceLocation PIGMAN = new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_pigman_pony.png");
    public static final ResourceLocation SKELETON = new ResourceLocation(NAMESPACE, "textures/entity/skeleton/skeleton_pony.png");

    public static final Map<SkeletonType, ResourceLocation> SKELETONS = Maps.immutableEnumMap(ImmutableMap.<SkeletonType, ResourceLocation> builder()
            .put(SkeletonType.NORMAL, SKELETON)
            .put(SkeletonType.WITHER, new ResourceLocation(NAMESPACE, "textures/entity/skeleton/skeleton_wither_pony.png"))
            .put(SkeletonType.STRAY, new ResourceLocation(NAMESPACE, "testures/entity/skeleton/stray_pony.png"))
            .build());
    public static final ResourceLocation STEVE = new ResourceLocation(NAMESPACE, "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation(NAMESPACE, "textures/entity/alex_pony.png");

    private static final ResourceLocation BGPONIES_JSON = new ResourceLocation(NAMESPACE, "textures/entity/pony/bgponies.json");
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

    public Pony getPonyFromResourceRegistry(EntityVillager entity) {
        int profession = entity.getProfession();

        ResourceLocation villagerResourceLocation;
        try {
            villagerResourceLocation = VILLAGER_LIST.get(profession);
        } catch (IndexOutOfBoundsException var5) {
            villagerResourceLocation = VILLAGER_LIST.get(5);
        }

        Pony myLittlePony = this.getPonyFromResourceRegistry(villagerResourceLocation);
        // myLittlePony.setVillager(profession);
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

    public static ResourceLocation getDefaultSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 0 ? STEVE : ALEX;
    }

    public int getNumberOfPonies() {
        return backgroundPonyList.size();
    }

    private static class BackgroundPonies implements Function<String, ResourceLocation> {

        public boolean override;
        private List<String> ponies;

        @Override
        public ResourceLocation apply(String input) {
            return new ResourceLocation(NAMESPACE, String.format("textures/entity/pony/%s.png", input));
        }

        public List<ResourceLocation> getPonies() {
            return Lists.transform(ponies, this);
        }
    }
}
