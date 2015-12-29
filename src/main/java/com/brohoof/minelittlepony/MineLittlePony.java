package com.brohoof.minelittlepony;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.brohoof.minelittlepony.gui.MineLittlePonyGUI;
import com.brohoof.minelittlepony.gui.MineLittlePonyGUIMob;
import com.brohoof.minelittlepony.hdskins.gui.EntityPonyModel;
import com.brohoof.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.brohoof.minelittlepony.hdskins.gui.RenderPonyModel;
import com.brohoof.minelittlepony.renderer.RenderPonySkeleton;
import com.brohoof.minelittlepony.renderer.RenderPonyVillager;
import com.brohoof.minelittlepony.renderer.RenderPonyZombie;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.common.properties.gui.SettingsPanelManager;
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

public class MineLittlePony implements InitCompleteListener {

    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_NAME = "Mine Little Pony";
    public static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    public static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    public static final String UPLOAD_URL = "http://minelpskinmanager.voxelmodpack.com/";
    private static final KeyBinding guiKeybinding = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");
    private static final KeyBinding skinKeybinding = new KeyBinding("Skin Manager", Keyboard.KEY_F1, "Mine Little Pony");

    private static MineLittlePony instance;

    private PonyConfig config;
    private PonyManager ponyManager;
    private ProxyContainer proxy;

    public MineLittlePony() {
        instance = this;
    }

    public static MineLittlePony getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return MOD_NAME;
    }

    @Override
    public String getVersion() {
        return MOD_VERSION;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(guiKeybinding);
        LiteLoader.getInput().registerKeyBinding(skinKeybinding);

        SettingsPanelManager.addSettingsPanel("Pony", MineLittlePonyGUI.class);
        SettingsPanelManager.addSettingsPanel("Pony Mobs", MineLittlePonyGUIMob.class);

        this.config = new PonyConfig();
        this.ponyManager = new PonyManager(config);
        this.proxy = new ProxyContainer();

        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        if (this.config.getHd().get()) {
            HDSkinManager.clearSkinCache();
            HDSkinManager.setSkinUrl(SKIN_SERVER_URL);
            HDSkinManager.setGatewayURL(GATEWAY_URL);
            HDSkinManager.addSkinModifier(new PonySkinModifier());
            MineLPLogger.info("Set MineLP skin server URL.");
        }
        RenderManager rm = minecraft.getRenderManager();
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));
        if (this.config.getVillagers().get()) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            MineLPLogger.info("Villagers are now ponies.");
        }

        if (this.config.getZombies().get()) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombies are now ponies.");
        }

        if (this.config.getPigZombies().get()) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombie pigmen are now ponies.");
        }

        if (this.config.getSkeletons().get()) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton(rm));
            MineLPLogger.info("Skeletons are now ponies.");
        }

    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame && minecraft.currentScreen == null && guiKeybinding.isPressed()) {
            minecraft.displayGuiScreen(new MineLittlePonyGUI());
        }

        boolean pressed = minecraft.currentScreen instanceof GuiMainMenu
                && Keyboard.isKeyDown(skinKeybinding.getKeyCode());
        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (pressed || skins) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
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
