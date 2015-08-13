package com.minelittlepony.minelp;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.minelittlepony.minelp.gui.MineLittlePonyGUI;
import com.minelittlepony.minelp.gui.MineLittlePonyGUIMob;
import com.minelittlepony.minelp.hdskins.gui.EntityPonyModel;
import com.minelittlepony.minelp.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.minelp.hdskins.gui.RenderPonyModel;
import com.minelittlepony.minelp.renderer.RenderPonySkeleton;
import com.minelittlepony.minelp.renderer.RenderPonyVillager;
import com.minelittlepony.minelp.renderer.RenderPonyZombie;
import com.minelittlepony.minelp.util.MineLPLogger;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.common.properties.ModConfig;
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
    public static final String MOD_VERSION = "1.8-UNOFFICIAL";
    public static final String MOD_NAME = "Mine Little Pony";
    public static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    public static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    public static final String UPLOAD_URL = "http://minelpskinmanager.voxelmodpack.com/";
    private static final KeyBinding guiKeybinding = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");
    private static final KeyBinding skinKeybinding = new KeyBinding("Skin Manager", Keyboard.KEY_F1, "Mine Little Pony");
    private PonyConfig config;
    private PonyManager ponyManager;
    private String spUsername;
    private static MineLittlePony instance;

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
        this.ponyManager = PonyManager.getInstance();
        this.config = new PonyConfig();

        int readInt = this.config.getIntPropertySafe("ponylevel", 0, 2);
        this.ponyManager.setPonyLevel(PonyLevel.parse(readInt));
        MineLPLogger.info("Pony level is set to %d.", Integer.valueOf(readInt));

        readInt = this.config.getIntPropertySafe("sizes", 0, 1);
        this.ponyManager.setUseSizes(readInt);
        MineLPLogger.info("Different pony sizes are %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("ponyarmor", 0, 1);
        this.ponyManager.setPonyArmor(readInt);
        MineLPLogger.info("Pony armor is %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("snuzzles", 0, 1);
        this.ponyManager.setShowSnuzzles(readInt);
        MineLPLogger.info("Snuzzels are %s.", readInt == 0 ? "disabled (You are a bad pony)" : "enabled");

        readInt = this.config.getIntPropertySafe("hd", 0, 1);
        this.ponyManager.setHD(readInt);
        MineLPLogger.info("MineLittlePony skin server is %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("showscale", 0, 1);
        this.ponyManager.setShowScale(readInt);
        MineLPLogger.info("Show-accurate scaling is %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("villagers", 0, 1);
        this.ponyManager.setPonyVillagers(readInt);
        MineLPLogger.info("Pony villagers are %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("zombies", 0, 1);
        this.ponyManager.setPonyZombies(readInt);
        MineLPLogger.info("Pony zombies are %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("pigzombies", 0, 1);
        this.ponyManager.setPonyPigzombies(readInt);
        MineLPLogger.info("Pony pigzombies are %s.", readInt == 0 ? "disabled" : "enabled");

        readInt = this.config.getIntPropertySafe("skeletons", 0, 1);
        this.ponyManager.setPonySkeletons(readInt);
        MineLPLogger.info("Pony skeletons are %s.", readInt == 0 ? "disabled" : "enabled");
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        this.spUsername = minecraft.getSession().getUsername();
        if (this.ponyManager.getHD() == 1) {
            HDSkinManager.clearSkinCache();
            HDSkinManager.setSkinUrl(SKIN_SERVER_URL);
            HDSkinManager.setGatewayURL(GATEWAY_URL);
            MineLPLogger.info("Set MineLP skin server URL.");
        }
        RenderManager rm = minecraft.getRenderManager();
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));
        if (this.ponyManager.getPonyVillagers() == 1) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            MineLPLogger.info("Villagers are now ponies.");
        }

        if (this.ponyManager.getPonyZombies() == 1) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombies are now ponies.");
        }

        if (this.ponyManager.getPonyPigzombies() == 1) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyZombie(rm));
            MineLPLogger.info("Zombie pigmen are now ponies.");
        }

        if (this.ponyManager.getPonySkeletons() == 1) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton(rm));
            MineLPLogger.info("Skeletons are now ponies.");
        }

    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        this.ponyManager.setPonyLevel(PonyLevel.parse(this.config.getIntPropertySafe("ponylevel", 0, 2)));
        this.ponyManager.setUseSizes(this.config.getIntPropertySafe("sizes"));
        this.ponyManager.setPonyArmor(this.config.getIntPropertySafe("ponyarmor"));
        this.ponyManager.setShowSnuzzles(this.config.getIntPropertySafe("snuzzles"));
        this.ponyManager.setShowScale(this.config.getIntPropertySafe("showscale"));
        if (inGame && minecraft.currentScreen == null && guiKeybinding.isPressed()) {
                minecraft.displayGuiScreen(new MineLittlePonyGUI());
        }
        // if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
        // ponyManager.initmodels();
        // }

        if (!inGame && minecraft.currentScreen != null
                && (minecraft.currentScreen instanceof GuiMainMenu && Keyboard.isKeyDown(skinKeybinding.getKeyCode())
                        || minecraft.currentScreen instanceof GuiSkins)) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP());
        }

    }

    public static ModConfig getConfig() {
        return getInstance().config;
    }

    public static String getSPUsername() {
        return getInstance().spUsername;
    }
}
