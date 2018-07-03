package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyDataSerialzier;
import com.minelittlepony.render.PonySkullRenderer;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.CubeMapRegistry;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
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

    private static final String MINELP_LEGACY_SERVER = "legacy:http://minelpskins.voxelmodpack.com;http://minelpskinmanager.voxelmodpack.com";

    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private final PonyConfig config;
    private final PonyManager ponyManager;

    private final PonyRenderManager renderManager;

    MineLittlePony() {
        instance = this;

        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);

        config = new PonyConfig();
        ponyManager = new PonyManager(config);

        renderManager = new PonyRenderManager();

        LiteLoader.getInstance().registerExposable(config, null);
        LiteLoader.getInstance().writeConfig(config);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialzier(), IPonyData.class);

        // This also makes it the default gateway server.
        SkinServer.defaultServers.add(MINELP_LEGACY_SERVER);

        for (String resource : config.panoramas) {
            CubeMapRegistry.addSource(resource);
        }
    }

    /**
     * Called when the game is ready.
     */
    void postInit(Minecraft minecraft) {

        HDSkinManager manager = HDSkinManager.INSTANCE;
//        manager.setSkinUrl(SKIN_SERVER_URL);
//        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
//        logger.info("Set MineLP skin server URL.");
        manager.addClearListener(ponyManager);

        manager.setPrefferedSkinsGuiClass(GuiSkinsMineLP.class);

        RenderManager rm = minecraft.getRenderManager();
        renderManager.initialisePlayerRenderers(rm);
        renderManager.initializeMobRenderers(rm, config);
    }

    void onTick(Minecraft minecraft, boolean inGame) {
        if (inGame && minecraft.currentScreen == null && SETTINGS_GUI.isPressed()) {
            minecraft.displayGuiScreen(new GuiPonySettings());
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

    /**
     * Gets the global MineLP client configuration.
     */
    public static PonyConfig getConfig() {
        return getInstance().config;
    }

}
