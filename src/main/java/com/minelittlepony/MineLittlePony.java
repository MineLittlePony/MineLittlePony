package com.minelittlepony;

import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyDataSerialzier;
import com.minelittlepony.render.ponies.MobRenderers;
import com.minelittlepony.settings.PonyConfig;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "minelittlepony", name = MineLittlePony.MOD_NAME, version = MineLittlePony.MOD_VERSION, clientSideOnly = true)
public class MineLittlePony {

    public static Logger logger;

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    private static final String MINELP_LEGACY_SERVER = "legacy:http://minelpskins.voxelmodpack.com;http://minelpskinmanager.voxelmodpack.com";

    private static MineLittlePony instance;

    private PonyManager ponyManager;

    private PonyRenderManager renderManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);

        ConfigManager.sync("minelittlepony", Config.Type.INSTANCE);

        ponyManager = new PonyManager();

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
        renderManager.initializeMobRenderers();
    }

    /**
     * Called on every update tick
     */
    @SubscribeEvent
    public void onTick(GuiScreenEvent.InitGuiEvent.Pre event) {

        if (event.getGui() instanceof GuiSkins && !(event.getGui() instanceof GuiSkinsMineLP)) {
            event.setCanceled(true);
            Minecraft.getMinecraft().displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
        HDSkinManager.INSTANCE.setEnabled(PonyConfig.hd);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//        System.out.println("Config " + event.getModID() + "." + event.getConfigID() + " Loaded");
        if (event.getModID().equals("minelittlepony")) {
            ConfigManager.sync("minelittlepony", Config.Type.INSTANCE);
            for (MobRenderers mobRenderers : MobRenderers.values()) {
                mobRenderers.set(mobRenderers.get());
            }
            renderManager.initializeMobRenderers();
        }
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

}
