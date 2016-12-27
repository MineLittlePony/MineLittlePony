package com.minelittlepony;

import com.google.common.collect.Maps;
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
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
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

import java.util.Map;

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

    private Map<Class<? extends Entity>, Render<?>> renderMap = Maps.newHashMap();

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

        HDSkinManager manager = HDSkinManager.INSTANCE;
        manager.setSkinUrl(SKIN_SERVER_URL);
        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
        logger.info("Set MineLP skin server URL.");

        RenderManager rm = minecraft.getRenderManager();
        this.saveCurrentRenderers(rm);
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));
        this.initializeMobRenderers(rm);

    }

    private void saveCurrentRenderers(RenderManager rm) {
        // villagers
        this.renderMap.put(EntityVillager.class, rm.getEntityClassRenderObject(EntityVillager.class));
        this.renderMap.put(EntityZombieVillager.class, rm.getEntityClassRenderObject(EntityZombieVillager.class));
        // zombies
        this.renderMap.put(EntityZombie.class, rm.getEntityClassRenderObject(EntityZombie.class));
        this.renderMap.put(EntityHusk.class, rm.getEntityClassRenderObject(EntityHusk.class));
        // pig zombie
        this.renderMap.put(EntityPigZombie.class, rm.getEntityClassRenderObject(EntityPigZombie.class));
        // skeletons
        this.renderMap.put(EntitySkeleton.class, rm.getEntityClassRenderObject(EntitySkeleton.class));
        this.renderMap.put(EntityStray.class, rm.getEntityClassRenderObject(EntityStray.class));
        this.renderMap.put(EntityWitherSkeleton.class, rm.getEntityClassRenderObject(EntityWitherSkeleton.class));
        // illagers
        this.renderMap.put(EntityVex.class, rm.getEntityClassRenderObject(EntityVex.class));
        this.renderMap.put(EntityEvoker.class, rm.getEntityClassRenderObject(EntityEvoker.class));
        this.renderMap.put(EntityVindicator.class, rm.getEntityClassRenderObject(EntityVindicator.class));
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> Render<T> getRenderer(Class<T> cl) {
        Render<T> render = (Render<T>) this.renderMap.get(cl);
        if (render == null)
            throw new MissingRendererException(cl);
        return render;
    }

    public void initializeMobRenderers(RenderManager rm) {
        if (this.config.villagers) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            ModUtilities.addRenderer(EntityZombieVillager.class, new RenderPonyZombieVillager(rm));
            logger.info("Villagers are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityVillager.class, getRenderer(EntityVillager.class));
            ModUtilities.addRenderer(EntityZombieVillager.class, getRenderer(EntityZombieVillager.class));
        }

        if (this.config.zombies) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie<>(rm));
            ModUtilities.addRenderer(EntityHusk.class, new RenderPonyZombie.Husk(rm));
            logger.info("Zombies are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityZombie.class, getRenderer(EntityZombie.class));
            ModUtilities.addRenderer(EntityHusk.class, getRenderer(EntityHusk.class));
        }

        if (this.config.pigzombies) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyPigman(rm));
            logger.info("Zombie pigmen are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityPigZombie.class, getRenderer(EntityPigZombie.class));
        }

        if (this.config.skeletons) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton<>(rm));
            ModUtilities.addRenderer(EntityStray.class, new RenderPonySkeleton.Stray(rm));
            ModUtilities.addRenderer(EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(rm));
            logger.info("Skeletons are now ponies.");
        } else {
            ModUtilities.addRenderer(EntitySkeleton.class, getRenderer(EntitySkeleton.class));
            ModUtilities.addRenderer(EntityStray.class, getRenderer(EntityStray.class));
            ModUtilities.addRenderer(EntityWitherSkeleton.class, getRenderer(EntityWitherSkeleton.class));
        }

        if (this.config.illagers) {
            ModUtilities.addRenderer(EntityVex.class, new RenderPonyVex(rm));
            ModUtilities.addRenderer(EntityEvoker.class, new RenderPonyEvoker(rm));
            ModUtilities.addRenderer(EntityVindicator.class, new RenderPonyVindicator(rm));
            logger.info("Illagers are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityVex.class, getRenderer(EntityVex.class));
            ModUtilities.addRenderer(EntityEvoker.class, getRenderer(EntityEvoker.class));
            ModUtilities.addRenderer(EntityVindicator.class, getRenderer(EntityVindicator.class));
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
