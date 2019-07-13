package com.minelittlepony.client;

import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.hdskins.IndirectHDSkins;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.common.config.ConfigManager;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.common.event.ScreenInitCallback;
import com.minelittlepony.common.event.SkinFilterCallback;
import com.minelittlepony.pony.IPonyManager;
import com.minelittlepony.settings.PonyConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * Client Mod implementation
 */
public class MineLittlePony implements ClientModInitializer {

    private static MineLittlePony instance;

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    private int modelUpdateCounter = 0;
    private boolean reloadingModels = false;

    private final PonyRenderManager renderManager = PonyRenderManager.getInstance();

    private PonyManager ponyManager;

    private FabricKeyBinding keyBinding;

    private boolean hasHdSkins;
    private boolean hasModMenu;

    public MineLittlePony() {
        instance = this;
        ConfigManager.register("minelp.json", PonyConfig.INSTANCE);
    }

    /**
     * Gets the global MineLP instance.
     */
    public static MineLittlePony getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        hasHdSkins = FabricLoader.getInstance().isModLoaded("hdskins");
        hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");


        ponyManager = new PonyManager();
        keyBinding = FabricKeyBinding.Builder.create(new Identifier("minelittlepony", "settings"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.misc").build();

        KeyBindingRegistry.INSTANCE.register(keyBinding);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ponyManager);

        // convert legacy pony skins
        SkinFilterCallback.EVENT.register(new LegacySkinConverter());

        // general events
        ClientReadyCallback.Handler.register();
        ClientTickCallback.EVENT.register(this::onTick);
        ClientReadyCallback.EVENT.register(this::onClientReady);
        ScreenInitCallback.EVENT.register(this::onScreenInit);

        if (FabricLoader.getInstance().isModLoaded("hdskins")) {
            IndirectHDSkins.initialize();
        }
    }

    private void onClientReady(MinecraftClient client) {
        renderManager.initialiseRenderers(client.getEntityRenderManager());
    }

    private void onTick(MinecraftClient client) {

        boolean inGame = client.world != null && client.player != null && client.currentScreen == null;
        boolean mainMenu = client.currentScreen instanceof TitleScreen;

        if (!inGame && mainMenu) {
            KeyBinding.updatePressedStates();
        }

        if ((mainMenu || inGame) && keyBinding.isPressed()) {
            client.openScreen(new GuiPonySettings());
        } else if (inGame) {
            long handle = client.window.getHandle();

            if ((SystemUtil.getMeasuringTimeMs() % 10) == 0) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_F3) && InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_M)) {
                    if (!reloadingModels) {
                        client.inGameHud.getChatHud().addMessage(
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

    private void onScreenInit(Screen screen, ScreenInitCallback.ButtonList buttons) {
        if (screen instanceof TitleScreen) {
            VisibilityMode mode = PonyConfig.INSTANCE.horseButton.get();
            boolean show = mode == VisibilityMode.ON || (mode == VisibilityMode.AUTO
                && !(hasHdSkins || hasModMenu
            ));

            if (show) {
                int y = hasHdSkins ? 80 : 50;
                buttons.add(new Button(screen.width - 50, screen.height - y, 20, 20).onClick(sender -> {
                    MinecraftClient.getInstance().openScreen(new GuiPonySettings());
                }).setStyle(new com.minelittlepony.common.client.gui.style.Style()
                        .setIcon(new TextureSprite()
                                .setPosition(2, 2)
                                .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                                .setTextureSize(16, 16)
                                .setSize(16, 16))
                ));
            }
        }
    }

    public IPonyManager getManager() {
        return ponyManager;
    }

    public int getModelRevision() {
        return modelUpdateCounter;
    }
}

