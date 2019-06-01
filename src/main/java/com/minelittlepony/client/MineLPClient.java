package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.settings.PonyConfig;

import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MainMenuScreen;
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

    private static int modelUpdateCounter = 0;
    private static boolean reloadingModels = false;

    private PonyConfig config;
    private PonyManager ponyManager;

    private final IModUtilities utilities;

    private final PonyRenderManager renderManager = PonyRenderManager.getInstance();

    private KeyBinding keyBinding;

    public static MineLPClient getInstance() {
        return (MineLPClient)MineLittlePony.getInstance();
    }

    public MineLPClient(IModUtilities utils) {
        utilities = utils;
    }

    protected void init(PonyConfig newConfig) {
        config = newConfig;
        ponyManager = new PonyManager(config);
        keyBinding = utilities.registerKeybind("key.categories.misc", GLFW.GLFW_KEY_F9, "key.minelittlepony.settings");
    }

    /**
     * Called when the game is ready.
     */
    public void postInit(MinecraftClient minecraft) {
        EntityRenderDispatcher rm = minecraft.getEntityRenderManager();
        renderManager.initialiseRenderers(rm);

        ReloadableResourceManager irrm = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
        irrm.registerListener(ponyManager);
    }

    public void onTick(MinecraftClient minecraft, boolean inGame) {

        inGame &= minecraft.currentScreen == null;

        boolean mainMenu = minecraft.currentScreen instanceof MainMenuScreen;

        if (!inGame && mainMenu) {
            KeyBinding.updatePressedStates();
        }

        if ((mainMenu || inGame) && keyBinding.isPressed()) {
            minecraft.openScreen(new GuiPonySettings());
        } else if (inGame) {
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
