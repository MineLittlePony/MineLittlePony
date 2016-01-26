package com.brohoof.minelittlepony;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.brohoof.minelittlepony.gui.PonySettingPanel;
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

public class LiteModMineLittlePony implements InitCompleteListener {

    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_NAME = "Mine Little Pony";
    public static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    public static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    public static final String UPLOAD_URL = "http://minelpskinmanager.voxelmodpack.com/";
    private static final KeyBinding guiKeybinding = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");
    private static final KeyBinding skinKeybinding = new KeyBinding("Skin Manager", Keyboard.KEY_F1, "Mine Little Pony");

    private PonyConfig config;
    private PonyManager ponyManager;

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

        // SettingsPanelManager.addSettingsPanel("Pony",
        // MineLittlePonyGUI.class);
        // SettingsPanelManager.addSettingsPanel("Pony Mobs",
        // MineLittlePonyGUIMob.class);
        
        this.config = MineLittlePony.getConfig();
        this.ponyManager = MineLittlePony.getInstance().getManager();
        
        LiteLoader.getInstance().registerExposable(config, null);
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {

        HDSkinManager.clearSkinCache();
        HDSkinManager manager = HDSkinManager.INSTANCE;
        manager.setSkinUrl(config.skinfix ? GATEWAY_URL : SKIN_SERVER_URL);
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

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame && minecraft.currentScreen == null && guiKeybinding.isPressed()) {
            minecraft.displayGuiScreen(new PonySettingPanel());
        }

        boolean pressed = minecraft.currentScreen instanceof GuiMainMenu
                && Keyboard.isKeyDown(skinKeybinding.getKeyCode());
        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (pressed || skins) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
        HDSkinManager.INSTANCE.setEnabled(config.hd);
    }

}
