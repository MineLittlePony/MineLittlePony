package com.brohoof.minelittlepony;

import org.lwjgl.input.Keyboard;

import com.brohoof.minelittlepony.gui.PonySettingPanel;
import com.brohoof.minelittlepony.hdskins.gui.EntityPonyModel;
import com.brohoof.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.brohoof.minelittlepony.hdskins.gui.RenderPonyModel;
import com.brohoof.minelittlepony.renderer.RenderPonySkeleton;
import com.brohoof.minelittlepony.renderer.RenderPonyVillager;
import com.brohoof.minelittlepony.renderer.RenderPonyZombie;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiSkins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;

public class MineLittlePony {

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    private static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    private static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");
    private static final KeyBinding SKIN_MANAGER = new KeyBinding("Skin Manager", Keyboard.KEY_F1, "Mine Little Pony");

    private static MineLittlePony instance;

    private PonyConfig config;
    private PonyManager ponyManager;
    private ProxyContainer proxy;

    MineLittlePony() {
        instance = this;
    }

    void init() {
        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);
        LiteLoader.getInput().registerKeyBinding(SKIN_MANAGER);

        this.config = new PonyConfig();
        this.ponyManager = new PonyManager(config);
        this.proxy = new ProxyContainer();

        LiteLoader.getInstance().registerExposable(config, null);
    }

    void postInit(Minecraft minecraft) {

        HDSkinManager.clearSkinCache();
        HDSkinManager manager = HDSkinManager.INSTANCE;
        manager.setSkinUrl(SKIN_SERVER_URL);
        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
        MineLPLogger.info("Set MineLP skin server URL.");

        RenderManager rm = minecraft.getRenderManager();
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));
        if (this.config.villagers) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            MineLPLogger.info("Villagers are now ponies.");
        }

        if (this.config.zombies) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombies are now ponies.");
        }

        if (this.config.pigzombies) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombie pigmen are now ponies.");
        }

        if (this.config.skeletons) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton(rm));
            MineLPLogger.info("Skeletons are now ponies.");
        }

    }

    void onTick(Minecraft minecraft, boolean inGame) {
        if (inGame && minecraft.currentScreen == null && SETTINGS_GUI.isPressed()) {
            minecraft.displayGuiScreen(new PonySettingPanel());
        }

        boolean pressed = minecraft.currentScreen instanceof GuiMainMenu
                && Keyboard.isKeyDown(SKIN_MANAGER.getKeyCode());
        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (pressed || skins) {
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

    public static String getSPUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

}
