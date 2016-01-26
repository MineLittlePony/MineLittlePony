package com.brohoof.minelittlepony;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class PonyManager {

    private static final String NAMESPACE = "minelittlepony";
    public static final ResourceLocation ZOMBIE = new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_pony.png");
    public static final ResourceLocation ZOMBIE_VILLAGER = new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_villager_pony.png");
    public static final ResourceLocation PIGMAN = new ResourceLocation(NAMESPACE, "textures/entity/zombie/zombie_pigman_pony.png");
    public static final ResourceLocation SKELETON = new ResourceLocation(NAMESPACE, "textures/entity/skeleton/skeleton_pony.png");
    public static final ResourceLocation WITHER_SKELETON = new ResourceLocation(NAMESPACE, "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final ResourceLocation STEVE = new ResourceLocation(NAMESPACE, "textures/entity/steve_pony.png");
    public static final ResourceLocation ALEX = new ResourceLocation(NAMESPACE, "textures/entity/alex_pony.png");

    private static final int MAX_BGPONY_COUNT = 141;

    private final List<ResourceLocation> backgroundPonyList = makeBkgndPonies();
    private final List<ResourceLocation> villagerList = ImmutableList.<ResourceLocation> builder()
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/farmer_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/librarian_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/priest_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/smith_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/butcher_pony.png"))
            .add(new ResourceLocation(NAMESPACE, "textures/entity/villager/villager_pony.png"))
            .build();

    private static List<ResourceLocation> makeBkgndPonies() {
        ImmutableList.Builder<ResourceLocation> list = ImmutableList.builder();
        for (int check = 0; check < MAX_BGPONY_COUNT; ++check) {
            list.add(new ResourceLocation(NAMESPACE, "textures/entity/pony/bpony_" + check + ".png"));
        }
        return list.build();
    }

    private PonyConfig config;

    private Map<ResourceLocation, Pony> ponies = Maps.newHashMap();
    private Map<ResourceLocation, Pony> backgroudPonies = Maps.newHashMap();

    public PonyManager(PonyConfig config) {
        this.config = config;
        initmodels();
        MineLPLogger.info("Detected %d of %d background ponies installed.", getNumberOfPonies(), MAX_BGPONY_COUNT);
    }

    public void initmodels() {
        MineLPLogger.info("Initializing models...");
        PMAPI.init();
        MineLPLogger.info("Done initializing models.");
    }

    private Pony getPonyFromResourceRegistry(ResourceLocation skinResourceLocation, AbstractClientPlayer player) {
        Pony myLittlePony;
        if (!this.ponies.containsKey(skinResourceLocation)) {
            if (player != null) {
                myLittlePony = new Pony(player);
            } else {
                myLittlePony = new Pony(skinResourceLocation);
            }

            this.ponies.put(skinResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.ponies.get(skinResourceLocation);
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
            villagerResourceLocation = villagerList.get(profession);
        } catch (IndexOutOfBoundsException var5) {
            villagerResourceLocation = villagerList.get(5);
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
        if (!this.backgroudPonies.containsKey(textureResourceLocation)) {
            myLittlePony = new Pony(textureResourceLocation);
            this.backgroudPonies.put(textureResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.backgroudPonies.get(textureResourceLocation);
        }

        return myLittlePony;
    }
    
    public static ResourceLocation getDefaultSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 0 ? STEVE : ALEX;
    }

    public int getNumberOfPonies() {
        return backgroundPonyList.size();
    }
}
