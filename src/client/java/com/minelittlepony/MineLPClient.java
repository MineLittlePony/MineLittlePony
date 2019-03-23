package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.PonyData;
import com.minelittlepony.pony.data.PonyDataSerialiser;
import com.minelittlepony.pony.data.PonyManager;
import com.minelittlepony.render.skull.PonySkullRenderer;
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

import org.lwjgl.input.Keyboard;

/**
 * Static MineLittlePony singleton class. Everything's controlled from up here.
 */
public class MineLPClient extends MineLittlePony {

    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";
    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static int modelUpdateCounter = 0;
    private static boolean reloadingModels = false;

    private PonyConfig config;
    private PonyManager ponyManager;

    private final PonyRenderManager renderManager = PonyRenderManager.getInstance();

    void init(PonyConfig newConfig) {
        config = newConfig;
        ponyManager = new PonyManager(config);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialiser(), PonyData.class);

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
}
