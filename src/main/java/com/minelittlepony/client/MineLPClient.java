package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.gui.hdskins.GuiSkinsMineLP;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.common.client.gui.GuiHost;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.net.LegacySkinServer;
import com.minelittlepony.hdskins.net.SkinServer;
import com.minelittlepony.hdskins.net.ValhallaSkinServer;
import com.minelittlepony.settings.PonyConfig;

import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.SystemUtil;

import org.lwjgl.glfw.GLFW;

/**
 * Static MineLittlePony singleton class. Everything's controlled from up here.
 */
public class MineLPClient extends MineLittlePony {

    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";
    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", GLFW.GLFW_KEY_F9, "Mine Little Pony");

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

        ReloadableResourceManager irrm = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
        irrm.registerListener(ponyManager);

        // This also makes it the default gateway server.
        SkinServer.defaultServers.add(new LegacySkinServer(MINELP_LEGACY_SERVER, MINELP_LEGACY_GATEWAY));
        SkinServer.defaultServers.add(0, new ValhallaSkinServer(MINELP_VALHALLA_SERVER));
    }

    /**
     * Called when the game is ready.
     */
    void postInit(MinecraftClient minecraft) {

        HDSkins manager = HDSkins.getInstance();
//        manager.setSkinUrl(SKIN_SERVER_URL);
//        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
        manager.addSkinParser(new PonySkinParser());
//        logger.info("Set MineLP skin server URL.");
        manager.addClearListener(ponyManager);

        manager.setSkinsGui(GuiSkinsMineLP::new);

        EntityRenderDispatcher rm = minecraft.getEntityRenderManager();

        renderManager.initialiseRenderers(rm);
    }

    void onTick(MinecraftClient minecraft, boolean inGame) {
        if (inGame && minecraft.currentScreen == null) {
            if (SETTINGS_GUI.isPressed()) {
                minecraft.disconnect(new GuiHost(new GuiPonySettings()));
            } else {
                long handle = minecraft.window.getHandle();

                if ((SystemUtil.getMeasuringTimeMs() % 10) == 0) {
                    if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_F3) && InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_M)) {
                        if (!reloadingModels) {
                            minecraft.inGameHud.getChatHud().addMessage(
                                    (new TextComponent("")).append(
                                    new TranslatableComponent("debug.prefix")
                                        .setStyle(new Style().setColor(ChatFormat.YELLOW).setBold(true)))
                                    .append(" ")
                                    .append(new TranslatableComponent("minelp.debug.reload_models.message")));

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
