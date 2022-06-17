package com.minelittlepony.client;

import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.api.pony.network.fabric.Channel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.pony.VariatedTextureSupplier;
import com.minelittlepony.client.render.PonyRenderDispatcher;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.common.event.ScreenInitCallback;
import com.minelittlepony.common.event.SkinFilterCallback;
import com.minelittlepony.common.util.GamePaths;
import com.minelittlepony.settings.PonyConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * Client Mod implementation
 */
public class MineLittlePony implements ClientModInitializer {

    private static MineLittlePony instance;

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    private final PonyRenderDispatcher renderManager = PonyRenderDispatcher.getInstance();

    private ClientPonyConfig config;
    private PonyManager ponyManager;
    private VariatedTextureSupplier variatedTextures;

    private final KeyBinding keyBinding = new KeyBinding("key.minelittlepony.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "key.categories.misc");

    private boolean hasHdSkins;
    private boolean hasModMenu;

    public MineLittlePony() {
        instance = this;
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

        config = new ClientPonyConfig(GamePaths.getConfigDirectory().resolve("minelp.json"));
        ponyManager = new PonyManager(config);
        variatedTextures = new VariatedTextureSupplier();

        KeyBindingHelper.registerKeyBinding(keyBinding);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ponyManager);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(variatedTextures);

        // convert legacy pony skins
        SkinFilterCallback.EVENT.register(new LegacySkinConverter());

        // general events
        ClientReadyCallback.Handler.register();
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientReadyCallback.EVENT.register(this::onClientReady);
        ScreenInitCallback.EVENT.register(this::onScreenInit);

        config.load();

        Channel.bootstrap();
        ModelType.bootstrap();

        FabricLoader.getInstance().getEntrypoints("minelittlepony", ClientModInitializer.class).forEach(ClientModInitializer::onInitializeClient);
    }

    private void onClientReady(MinecraftClient client) {
        renderManager.initialise(client.getEntityRenderDispatcher());
    }

    private void onTick(MinecraftClient client) {

        boolean inGame = client.world != null && client.player != null && client.currentScreen == null;
        boolean mainMenu = client.currentScreen instanceof TitleScreen;

        if (!inGame && mainMenu) {
            KeyBinding.updatePressedStates();
        }

        if ((mainMenu || inGame) && keyBinding.isPressed()) {
            client.setScreen(new GuiPonySettings(client.currentScreen));
        }
    }

    private void onScreenInit(Screen screen, ScreenInitCallback.ButtonList buttons) {
        if (screen instanceof TitleScreen) {
            VisibilityMode mode = config.horseButton.get();
            boolean show = mode == VisibilityMode.ON || (mode == VisibilityMode.AUTO
                && !(hasHdSkins || hasModMenu
            ));

            if (show) {
                int y = hasHdSkins ? 75 : 50;
                Button button = buttons.addButton(new Button(screen.width - 50, screen.height - y, 20, 20))
                    .onClick(sender -> MinecraftClient.getInstance().setScreen(new GuiPonySettings(screen)));
                button.getStyle()
                        .setIcon(new TextureSprite()
                                .setPosition(2, 2)
                                .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                                .setTextureSize(16, 16)
                                .setSize(16, 16))
                        .setTooltip("minelp.options.title", 0, 10);
                button.y = screen.height - y; // ModMenu
            }
        }
    }

    /**
     * Gets the global MineLP client configuration.
     */
    public PonyConfig getConfig() {
        return config;
    }

    public IPonyManager getManager() {
        return ponyManager;
    }

    public VariatedTextureSupplier getVariatedTextures() {
        return variatedTextures;
    }
}

