package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.util.GamePaths;
import com.minelittlepony.settings.JsonConfig;
import com.minelittlepony.settings.PonyConfig;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
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

    private final PonyRenderManager renderManager = PonyRenderManager.getInstance();

    private FabricKeyBinding keyBinding;

    public static MineLPClient getInstance() {
        return (MineLPClient)MineLittlePony.getInstance();
    }

    public MineLPClient() {
        config = JsonConfig.of(GamePaths.getConfigDirectory().resolve("minelp.json"), this::createConfig);
        ponyManager = new PonyManager(config);

        keyBinding = FabricKeyBinding.Builder.create(new Identifier("minelittlepony", "settings"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.misc").build();
        KeyBindingRegistry.INSTANCE.register(keyBinding);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ponyManager);
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
    }

    public void onTick(MinecraftClient minecraft) {

        boolean inGame = minecraft.world != null && minecraft.player != null && minecraft.currentScreen == null;
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
