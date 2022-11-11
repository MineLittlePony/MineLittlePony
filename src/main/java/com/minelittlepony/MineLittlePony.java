package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyDataSerialiser;
import com.minelittlepony.pony.data.PonyManager;
import com.minelittlepony.render.skull.PonySkullRenderer;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.server.LegacySkinServer;
import com.voxelmodpack.hdskins.server.SkinServer;
import com.voxelmodpack.hdskins.server.ValhallaSkinServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

/**
 * Static MineLittlePony singleton class. Everything's controlled from up here.
 */
public class MineLittlePony {

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private final PonyConfig config;
    private final PonyManager ponyManager;

    private final PonyRenderManager renderManager;

    private static int modelUpdateCounter = 0;
    private static boolean reloadingModels = false;

    MineLittlePony() {
        instance = this;

        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);

        config = new PonyConfig();
        ponyManager = new PonyManager(config);

        renderManager = new PonyRenderManager();

        LiteLoader.getInstance().registerExposable(config, null);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialiser(), IPonyData.class);
    }

    /**
     * Called when the game is ready.
     */
    void postInit(Minecraft minecraft) {

        HDSkinManager manager = HDSkinManager.INSTANCE;
//        manager.setSkinUrl(SKIN_SERVER_URL);
//        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
        manager.addSkinParser(new PonySkinParser());
//        logger.info("Set MineLP skin server URL.");
        manager.addClearListener(ponyManager);

        manager.setSkinsGui(GuiSkinsMineLP::new);

        RenderManager rm = minecraft.getRenderManager();
        renderManager.initialisePlayerRenderers(rm);
        renderManager.initializeMobRenderers(rm, config);
    }

    void onTick(Minecraft minecraft, boolean inGame) {
        if (inGame && minecraft.currentScreen == null) {
            if (SETTINGS_GUI.isPressed()) {
                minecraft.displayGuiScreen(new GuiPonySettings());
            } else {

                if ((Minecraft.getSystemTime() % 10) == 0) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_M)) {
                        if (!reloadingModels) {
                            minecraft.ingameGUI.getChatGUI().printChatMessage(
                                    (new TextComponentString("")).appendSibling(
                                    new TextComponentTranslation("debug.prefix")
                                        .setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true)))
                                    .appendText(" ")
                                    .appendSibling(new TextComponentTranslation("minelp.debug.reload_models.message")));

                            reloadingModels = true;
                            modelUpdateCounter++;
                        }
                    } else {
                        reloadingModels = false;
                    }
                }
            }
        }

        PonySkullRenderer.resolve();
    }

    /**
     * Gets the global MineLP instance.
     */
    public static MineLittlePony getInstance() {
        return instance;
    }

    /**
     * Gets the static pony manager instance.
     */
    public PonyManager getManager() {
        return ponyManager;
    }

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public PonyRenderManager getRenderManager() {
        return renderManager;
    }

    public static int getModelRevisionNumber() {
        return modelUpdateCounter;
    }

    /**
     * Gets the global MineLP client configuration.
     */
    public static PonyConfig getConfig() {
        return getInstance().config;
    }

}
