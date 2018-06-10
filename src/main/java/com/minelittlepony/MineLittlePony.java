package com.minelittlepony;

import com.minelittlepony.gui.GuiPonySettings;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyDataSerialzier;
import com.minelittlepony.settings.PonyConfig;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;


@Mod(modid = "minelittlepony", name = MineLittlePony.MOD_NAME, version = MineLittlePony.MOD_VERSION, clientSideOnly = true)
public class MineLittlePony {

    public static Logger logger;

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    private static final String MINELP_LEGACY_SERVER = "legacy:http://minelpskins.voxelmodpack.com;http://minelpskinmanager.voxelmodpack.com";

    // TODO Replace this with a config screen
    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private PonyConfig.Loader configLoader;
    private PonyManager ponyManager;

    private PonyRenderManager renderManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        logger = event.getModLog();

        configLoader = new PonyConfig.Loader(event.getModConfigurationDirectory().toPath().resolve("minelittlepony.json"));
        ponyManager = new PonyManager(configLoader.getConfig());


        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialzier(), IPonyData.class);

        // This also makes it the default gateway server.
        SkinServer.defaultServers.add(MINELP_LEGACY_SERVER);
    }

    @Mod.EventHandler
    public void postInit(FMLLoadCompleteEvent event) {

        renderManager = new PonyRenderManager(Minecraft.getMinecraft().getRenderManager());

        HDSkinManager manager = HDSkinManager.INSTANCE;
//        manager.setSkinUrl(SKIN_SERVER_URL);
//        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
//        logger.info("Set MineLP skin server URL.");
        manager.addClearListener(ponyManager);

        renderManager.initialisePlayerRenderers();
        renderManager.initializeMobRenderers(configLoader.getConfig());
    }

    /**
     * Called on every update tick
     */
    void onTick(Minecraft minecraft, boolean inGame) {

        if (inGame && minecraft.currentScreen == null && SETTINGS_GUI.isPressed()) {
            minecraft.displayGuiScreen(new GuiPonySettings());
        }

        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (skins) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
        HDSkinManager.INSTANCE.setEnabled(configLoader.getConfig().hd);

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
    public static PonyConfig.Loader getConfigLoader() {
        return getInstance().configLoader;
    }

}
