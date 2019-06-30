package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.client.IModUtilities;
import com.minelittlepony.settings.JsonConfig;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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

        config = JsonConfig.of(utils.getConfigDirectory().resolve("minelp.json"), this::createConfig);
        ponyManager = new PonyManager(config);
        keyBinding = utilities.registerKeybind("key.categories.misc", "minelittlepony:settings", GLFW.GLFW_KEY_F9);
    }

    protected ClientPonyConfig createConfig() {
        return new ClientPonyConfig();
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

        boolean mainMenu = minecraft.currentScreen instanceof TitleScreen;

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
                                new LiteralText("").append(
                                new TranslatableText("debug.prefix")
                                    .setStyle(new Style().setColor(Formatting.YELLOW).setBold(true)))
                                .append(" ")
                                .append(new TranslatableText("minelp.debug.reload_models.message")));

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

    public Map<MinecraftProfileTexture.Type, Identifier> getProfileTextures(@Nullable GameProfile profile) {
        PlayerSkinProvider provider = MinecraftClient.getInstance().getSkinProvider();

        return provider.getTextures(profile)
            .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                return provider.loadSkin(entry.getValue(), entry.getKey());
            }));
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
