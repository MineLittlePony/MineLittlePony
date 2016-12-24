package com.minelittlepony;

import com.google.common.collect.Maps;
import com.minelittlepony.gui.PonySettingPanel;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.renderer.RenderPonyPigman;
import com.minelittlepony.renderer.RenderPonySkeleton;
import com.minelittlepony.renderer.RenderPonyVillager;
import com.minelittlepony.renderer.RenderPonyZombie;
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
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
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

        HDSkinManager.clearSkinCache();
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
        // zombies
        this.renderMap.put(EntityZombie.class, rm.getEntityClassRenderObject(EntityZombie.class));
        // pig zombie
        this.renderMap.put(EntityPigZombie.class, rm.getEntityClassRenderObject(EntityPigZombie.class));
        // skeletons
        this.renderMap.put(EntitySkeleton.class, rm.getEntityClassRenderObject(EntitySkeleton.class));
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
            logger.info("Villagers are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityVillager.class, getRenderer(EntityVillager.class));
        }

        if (this.config.zombies) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie<>(rm));
            logger.info("Zombies are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityZombie.class, getRenderer(EntityZombie.class));
        }

        if (this.config.pigzombies) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyPigman(rm));
            logger.info("Zombie pigmen are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityPigZombie.class, getRenderer(EntityPigZombie.class));
        }

        if (this.config.skeletons) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton<>(rm));
            logger.info("Skeletons are now ponies.");
        } else {
            ModUtilities.addRenderer(EntitySkeleton.class, getRenderer(EntitySkeleton.class));
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
