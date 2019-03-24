package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.gui.hdskins.GuiSkinsMineLP;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.common.client.gui.GuiHost;
import com.minelittlepony.hdskins.HDSkinManager;
import com.minelittlepony.hdskins.server.LegacySkinServer;
import com.minelittlepony.hdskins.server.SkinServer;
import com.minelittlepony.hdskins.server.ValhallaSkinServer;
import com.minelittlepony.settings.PonyConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * Static MineLittlePony singleton class. Everything's controlled from up here.
 */
public class MineLPClient extends MineLittlePony {

    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";
    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    public static final int KEY_M = 0x32;
    public static final int KEY_F3 = 0x3D;
    public static final int KEY_F9 = 0x43;
    static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", KEY_F9, "Mine Little Pony");

    private static int modelUpdateCounter = 0;
    private static boolean reloadingModels = false;

    private PonyConfig config;
    private PonyManager ponyManager;

    private final IModUtilities utilities;

    private final PonyRenderManager renderManager = PonyRenderManager.getInstance();

    public static MineLPClient getInstance() {
        return (MineLPClient)MineLittlePony.getInstance();
    }

    public MineLPClient(IModUtilities utils) {
        utilities = utils;
    }

    void init(PonyConfig newConfig) {
        config = newConfig;
        ponyManager = new PonyManager(config);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        irrm.addReloadListener(ponyManager);

        // This also makes it the default gateway server.
        SkinServer.defaultServers.add(new LegacySkinServer(MINELP_LEGACY_SERVER, MINELP_LEGACY_GATEWAY));
        SkinServer.defaultServers.add(0, new ValhallaSkinServer(MINELP_VALHALLA_SERVER));
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
                minecraft.displayGuiScreen(new GuiHost(new GuiPonySettings()));
            } else {

                if ((Util.milliTime() % 10) == 0) {
                    if (InputMappings.isKeyDown(KEY_F3) && InputMappings.isKeyDown(KEY_M)) {
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

    @Override
    public PonyManager getManager() {
        return ponyManager;
    }

    @Override
    public int getModelRevisionNumber() {
        return modelUpdateCounter;
    }

    /**
     * Gets the global MineLP client configuration.
     */
    @Override
    public PonyConfig getConfig() {
        return config;
    }

    public IModUtilities getModUtilities() {
        return utilities;
    }
}
