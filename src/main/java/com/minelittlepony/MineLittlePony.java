package com.minelittlepony;

import com.minelittlepony.gui.PonySettingPanel;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.renderer.RenderPonyEvoker;
import com.minelittlepony.renderer.RenderPonyPigman;
import com.minelittlepony.renderer.RenderPonySkeleton;
import com.minelittlepony.renderer.RenderPonyVex;
import com.minelittlepony.renderer.RenderPonyVillager;
import com.minelittlepony.renderer.RenderPonyVindicator;
import com.minelittlepony.renderer.RenderPonyZombie;
import com.minelittlepony.renderer.RenderPonyZombieVillager;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class MineLittlePony {

    public static final Logger logger = LogManager.getLogger("MineLittlePony");


    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    private static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    private static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private PonyConfig config;
    private PonyManager ponyManager;
    private ProxyContainer proxy;

    MineLittlePony() {
        instance = this;
    }

    void init() {
        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);

        this.config = new PonyConfig();
        this.ponyManager = new PonyManager(config);
        this.proxy = new ProxyContainer();

        LiteLoader.getInstance().registerExposable(config, null);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(this.ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialzier(), IPonyData.class);
    }

    void postInit(Minecraft minecraft) {

        HDSkinManager.clearSkinCache();
        HDSkinManager manager = HDSkinManager.INSTANCE;
        manager.setSkinUrl(SKIN_SERVER_URL);
        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
        logger.info("Set MineLP skin server URL.");

        RenderManager rm = minecraft.getRenderManager();
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));
        if (this.config.villagers) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            ModUtilities.addRenderer(EntityZombieVillager.class, new RenderPonyZombieVillager(rm));
            logger.info("Villagers are now ponies.");
        }

        if (this.config.zombies) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie<>(rm));
            ModUtilities.addRenderer(EntityHusk.class, new RenderPonyZombie.Husk(rm));
            logger.info("Zombies are now ponies.");
        }

        if (this.config.pigzombies) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyPigman(rm));
            logger.info("Zombie pigmen are now ponies.");
        }

        if (this.config.skeletons) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton<>(rm));
            ModUtilities.addRenderer(EntityStray.class, new RenderPonySkeleton.Stray(rm));
            ModUtilities.addRenderer(EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(rm));
            logger.info("Skeletons are now ponies.");
        }

        if (this.config.illagers) {
            ModUtilities.addRenderer(EntityVex.class, new RenderPonyVex(rm));
            ModUtilities.addRenderer(EntityEvoker.class, new RenderPonyEvoker(rm));
            ModUtilities.addRenderer(EntityVindicator.class, new RenderPonyVindicator(rm));
            logger.info("Illagers are now ponies.");
        }

    }

    void onTick(Minecraft minecraft, boolean inGame) {

        if (inGame && minecraft.currentScreen == null && SETTINGS_GUI.isPressed()) {
            minecraft.displayGuiScreen(new PonySettingPanel());
        }

        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (skins) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
        HDSkinManager.INSTANCE.setEnabled(config.hd);

    }

    public static MineLittlePony getInstance() {
        return instance;
    }

    public PonyManager getManager() {
        return this.ponyManager;
    }

    public static ProxyContainer getProxy() {
        return getInstance().proxy;
    }

    public static PonyConfig getConfig() {
        return getInstance().config;
    }

}
