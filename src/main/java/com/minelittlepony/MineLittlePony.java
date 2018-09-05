package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyDataSerialiser;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.PonySkullRenderer;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.skins.LegacySkinServer;
import com.voxelmodpack.hdskins.skins.SkinServer;
import com.voxelmodpack.hdskins.skins.ValhallaSkinServer;
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

    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";
    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private static PonyConfig pconfig;
    private static IPonyConfig config = new IPonyConfig() {
		@Override
		public PonyLevel getPonyLevel() {
			return PonyLevel.BOTH;
		}};
    private final PonyManager ponyManager;

    private static final PonyRenderManager renderManager = new PonyRenderManager();

    MineLittlePony() {
        instance = this;

        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);

        config = (IPonyConfig)(Object) (pconfig = new PonyConfig());
        ponyManager = new PonyManager(config);

        LiteLoader.getInstance().registerExposable(pconfig, null);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialiser(), IPonyData.class);

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
    public static PonyRenderManager getRenderManager() {
        return renderManager;
    }

    /**
     * Gets the global MineLP client configuration.
     */
    public static IPonyConfig getConfig() {
        return config;
    }

    /**
     * Gets the Exposable MineLP client config, which is only present if MineLP is loaded for LiteLoader
     */
    public static PonyConfig getInstalledConfig() {
    	return pconfig;
    }
}
