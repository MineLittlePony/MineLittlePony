package com.minelittlepony.minelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minelittlepony.minelp.model.PMAPI;
import com.minelittlepony.minelp.util.MineLPLogger;

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
    private Map<ResourceLocation, Pony> ponyResourceRegistry = new HashMap<ResourceLocation, Pony>();
    private Map<ResourceLocation, Pony> backgroudPonyResourceRegistry = new HashMap<ResourceLocation, Pony>();
    private PonyLevel ponyLevel = PonyLevel.PONIES;
    private int useSizes = 1;
    private int ponyArmor = 1;
    private int showSnuzzles = 1;
    private int showScale = 1;
    private int ponyVillagers = 1;
    private int ponyZombies = 1;
    private int ponyPigzombies = 1;
    private int ponySkeletons = 1;
    private int useHDSkinServer = 1;
    private static PonyManager instance;

    private PonyManager() {
        initmodels();
    }

    public void initmodels() {
        MineLPLogger.info("Initializing models...");
        PMAPI.init();
        MineLPLogger.info("Done initializing models.");
    }

    public static PonyManager getInstance() {
        if (instance == null) {
            instance = new PonyManager();
        }

        return instance;
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
        if (this.ponyLevel == PonyLevel.PONIES && !myLittlePony.isPonySkin()) {
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
        } else {
            return defaultPonyResourceLocation;
        }
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

    public int getHD() {
        return this.useHDSkinServer;
    }

    public int getNumberOfPonies() {
        return numberOfPonies;
    }

    public int getPonyArmor() {
        return this.ponyArmor;
    }

    public PonyLevel getPonyLevel() {
        return this.ponyLevel;
    }

    public int getPonyPigzombies() {
        return this.ponyPigzombies;
    }

    public int getPonySkeletons() {
        return this.ponySkeletons;
    }

    public int getPonyVillagers() {
        return this.ponyVillagers;
    }

    public int getPonyZombies() {
        return this.ponyZombies;
    }

    public int getShowScale() {
        return this.showScale;
    }

    public int getShowSnuzzles() {
        return this.showSnuzzles;
    }

    public int getUseSizes() {
        return this.useSizes;
    }

    public void setHD(int useHDSkinServer) {
        this.useHDSkinServer = useHDSkinServer;
    }

    public void setPonyArmor(int ponyArmor) {
        this.ponyArmor = ponyArmor;
    }

    public void setPonyLevel(PonyLevel ponyLevel) {
        this.ponyLevel = ponyLevel;
    }

    public void setPonyPigzombies(int ponyPigzombies) {
        this.ponyPigzombies = ponyPigzombies;
    }

    public void setPonySkeletons(int ponySkeletons) {
        this.ponySkeletons = ponySkeletons;
    }

    public void setPonyVillagers(int ponyVillagers) {
        this.ponyVillagers = ponyVillagers;
    }

    public void setPonyZombies(int ponyZombies) {
        this.ponyZombies = ponyZombies;
    }

    public void setShowScale(int showScale) {
        this.showScale = showScale;
    }

    public void setShowSnuzzles(int showSnuzzles) {
        this.showSnuzzles = showSnuzzles;
    }

    public void setUseSizes(int useSizes) {
        this.useSizes = useSizes;
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
