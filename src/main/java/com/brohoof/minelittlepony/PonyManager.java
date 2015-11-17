package com.brohoof.minelittlepony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.util.MineLPLogger;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class PonyManager {
    public static final String RESOURCE_NAMESPACE = "minelittlepony";
    public static final ResourceLocation zombiePonyResource = new ResourceLocation("minelittlepony",
            "textures/entity/zombie/zombie_pony.png");
    public static final ResourceLocation zombieVillagerPonyResource = new ResourceLocation("minelittlepony",
            "textures/entity/zombie/zombie_villager_pony.png");
    public static final ResourceLocation zombiePigmanPonyResource = new ResourceLocation("minelittlepony",
            "textures/entity/zombie_pigman_pony.png");
    public static final ResourceLocation skeletonPonyResource = new ResourceLocation("minelittlepony",
            "textures/entity/skeleton/skeleton_pony.png");
    public static final ResourceLocation skeletonWitherPonyResource = new ResourceLocation("minelittlepony",
            "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final ResourceLocation defaultPonyResourceLocation = new ResourceLocation("minelittlepony",
            "textures/entity/pony/charpony.png");
    public static List<ResourceLocation> backgroundPonyResourceLocations = new ArrayList<ResourceLocation>();
    public static List<ResourceLocation> villagerResourceLocations;
    private static final int MAX_BGPONY_COUNT = 141;
    private static int numberOfPonies;

    private PonyConfig config;

    private Map<ResourceLocation, Pony> ponyResourceRegistry = new HashMap<ResourceLocation, Pony>();
    private Map<ResourceLocation, Pony> backgroudPonyResourceRegistry = new HashMap<ResourceLocation, Pony>();

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
        if (!this.ponyResourceRegistry.containsKey(skinResourceLocation)) {
            if (player != null) {
                myLittlePony = new Pony(player);
            } else {
                myLittlePony = new Pony(skinResourceLocation);
            }

            this.ponyResourceRegistry.put(skinResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.ponyResourceRegistry.get(skinResourceLocation);
        }

        return myLittlePony;
    }

    public Pony getPonyFromResourceRegistry(ResourceLocation skinResourceLocation) {
        return this.getPonyFromResourceRegistry(skinResourceLocation, (AbstractClientPlayer) null);
    }

    public Pony getPonyFromResourceRegistry(AbstractClientPlayer player) {
        Pony myLittlePony = this.getPonyFromResourceRegistry(player.getLocationSkin(), player);
        if (config.getPonyLevel().get() == PonyLevel.PONIES && !myLittlePony.isPonySkin()) {
            myLittlePony = this.getPonyFromBackgroundResourceRegistry(player);
        }

        if (player.getCommandSenderName().equals(MineLittlePony.getSPUsername())) {
            myLittlePony.isSpPlayer = true;
        }

        return myLittlePony;
    }

    public Pony getPonyFromResourceRegistry(EntityVillager entity) {
        int profession = entity.getProfession();

        ResourceLocation villagerResourceLocation;
        try {
            villagerResourceLocation = villagerResourceLocations.get(profession);
        } catch (IndexOutOfBoundsException var5) {
            villagerResourceLocation = villagerResourceLocations.get(5);
        }

        Pony myLittlePony = this.getPonyFromResourceRegistry(villagerResourceLocation);
        myLittlePony.setVillager(profession);
        return myLittlePony;
    }

    private ResourceLocation getBackgroundPonyResource(String username) {
        if (numberOfPonies > 0) {
            int backgroundIndex = username.hashCode() % this.getNumberOfPonies();
            if (backgroundIndex < 0) {
                backgroundIndex += this.getNumberOfPonies();
            }

            return backgroundPonyResourceLocations.get(backgroundIndex);
        }
        return defaultPonyResourceLocation;
    }

    public Pony getPonyFromBackgroundResourceRegistry(AbstractClientPlayer player) {
        ResourceLocation textureResourceLocation;
        if (player.getCommandSenderName() == MineLittlePony.getSPUsername()) {
            textureResourceLocation = defaultPonyResourceLocation;
        } else {
            textureResourceLocation = this.getBackgroundPonyResource(player.getCommandSenderName());
        }

        Pony myLittlePony;
        if (!this.backgroudPonyResourceRegistry.containsKey(textureResourceLocation)) {
            myLittlePony = new Pony(textureResourceLocation);
            this.backgroudPonyResourceRegistry.put(textureResourceLocation, myLittlePony);
        } else {
            myLittlePony = this.backgroudPonyResourceRegistry.get(textureResourceLocation);
        }

        return myLittlePony;
    }

    public int getNumberOfPonies() {
        return numberOfPonies;
    }

    static {
        for (int check = 0; check < MAX_BGPONY_COUNT; ++check) {
            backgroundPonyResourceLocations
                    .add(new ResourceLocation("minelittlepony", "textures/entity/pony/bpony_" + check + ".png"));
        }

        numberOfPonies = backgroundPonyResourceLocations.size();
        MineLPLogger.info("Detected %d of %d background ponies installed.",
                new Object[] { Integer.valueOf(numberOfPonies), Integer.valueOf(MAX_BGPONY_COUNT) });
        villagerResourceLocations = new ArrayList<ResourceLocation>();
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/farmer_pony.png"));
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/librarian_pony.png"));
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/priest_pony.png"));
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/smith_pony.png"));
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/butcher_pony.png"));
        villagerResourceLocations
                .add(new ResourceLocation("minelittlepony", "textures/entity/villager/villager_pony.png"));
    }

    public static enum PonyRace {
        EARTH,
        PEGASUS,
        UNICORN,
        ALICORN,
        CHANGELING,
        ZEBRA;
    }
}
