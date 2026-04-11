package com.minelittlepony.client;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.ClientChannel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourTextureResolver;
import com.minelittlepony.client.render.*;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.common.event.ScreenInitCallback;
import com.minelittlepony.common.event.SkinFilterCallback;
import com.minelittlepony.common.util.GamePaths;
import com.mojang.blaze3d.platform.InputConstants;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * Client Mod implementation
 */
public class MineLittlePony implements ClientModInitializer {

    private static MineLittlePony instance;

    public static final Logger LOGGER = LogManager.getLogger("MineLittlePony");

    public static final Identifier PONY_HITBOXES_DEBUG_HUD_ENTRY = id("pony_hitboxes");
    public static final Identifier PONY_FILLYCAM_RAYS_DEBUG_HUD_ENTRY = id("pony_fillycam_rays");

    private PonyManagerImpl ponyManager;
    private VariatedTextureSupplier variatedTextures;

    private final KeyMapping keyBinding = new KeyMapping("key.minelittlepony.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F9, KeyMapping.Category.MISC);

    private final PonyRenderDispatcher renderDispatcher = new PonyRenderDispatcher();
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final AtomicBoolean configChanged = new AtomicBoolean();

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

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath("minelittlepony", name);
    }

    @Override
    public void onInitializeClient() {
        hasHdSkins = FabricLoader.getInstance().isModLoaded("hdskins");
        hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");

        PonyConfig config = new ClientPonyConfig(GamePaths.getConfigDirectory().resolve("minelp.json"));
        ponyManager = new PonyManagerImpl(config);
        variatedTextures = new VariatedTextureSupplier();

        KeyMappingHelper.registerKeyMapping(keyBinding);
        DebugScreenEntries.register(PONY_HITBOXES_DEBUG_HUD_ENTRY, new DebugEntryNoop());
        DebugScreenEntries.register(PONY_FILLYCAM_RAYS_DEBUG_HUD_ENTRY, new DebugEntryNoop());
        DebugScreenEntries.register(PonyEntityRenderersDebugEntry.ID, new PonyEntityRenderersDebugEntry());

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(PonyManagerImpl.ID, ponyManager);
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(VariatedTextureSupplier.ID, variatedTextures);
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ArmourTextureResolver.ID, ArmourTextureResolver.INSTANCE);

        // convert legacy pony skins
        SkinFilterCallback.EVENT.register(new LegacySkinConverter());

        // general events
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ScreenInitCallback.EVENT.register(this::onScreenInit);

        new ClientSkinsProxy();

        config.load();
        config.onChangedExternally(_ -> configChanged.set(true));

        ClientChannel.bootstrap();
        ModelType.bootstrap();
        MagicGlow.bootstrap();

        FabricLoader.getInstance().getEntrypoints("minelittlepony", ClientModInitializer.class).forEach(ClientModInitializer::onInitializeClient);
    }

    private void onTick(Minecraft client) {
        if (!initialized.getAndSet(true)) {
            renderDispatcher.initialise(client.getEntityRenderDispatcher(), false);
        }

        if (configChanged.getAndSet(false) && client.screen instanceof PonySettingsScreen screen) {
            screen.init(screen.width, screen.height);
        }

        boolean inGame = client.level != null && client.player != null && client.screen == null;
        boolean mainMenu = client.screen instanceof TitleScreen;

        if (!inGame && mainMenu) {
            KeyMapping.setAll();
        }

        if ((mainMenu || inGame) && keyBinding.isDown()) {
            client.setScreen(new PonySettingsScreen(client.screen));
        }
    }

    private void onScreenInit(Screen screen, ScreenInitCallback.ButtonList buttons) {
        if (screen instanceof TitleScreen) {
            VisibilityMode mode = ClientPonyConfig.getInstance().horseButton.get();
            boolean show = mode == VisibilityMode.ON || (mode == VisibilityMode.AUTO
                && !(hasHdSkins || hasModMenu
            ));

            if (show) {
                int y = hasHdSkins ? 75 : 50;
                Button button = buttons.addButton(new Button(screen.width - 50, screen.height - y, 20, 20))
                    .onClick(_ -> Minecraft.getInstance().setScreen(new PonySettingsScreen(screen)));
                button.getStyle()
                        .setIcon(new TextureSprite()
                                .setPosition(2, 2)
                                .setTexture(id("textures/gui/pony.png"))
                                .setTextureSize(16, 16)
                                .setSize(16, 16))
                        .setTooltip("minelp.options.title", 0, 10);
                button.setY(screen.height - y); // ModMenu
            }
        }
    }

    public PonyManagerImpl getManager() {
        return ponyManager;
    }

    public VariatedTextureSupplier getVariatedTextures() {
        return variatedTextures;
    }

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public PonyRenderDispatcher getRenderDispatcher() {
        return renderDispatcher;
    }

    private static final class ClientPonyConfig extends PonyConfig {
        public ClientPonyConfig(Path path) {
            super(path);
            MobRenderers.REGISTRY.values().forEach(r -> value("entities", r.name(), true));
            disablePonifiedArmour.onChanged(_ -> ArmourTextureResolver.INSTANCE.invalidate());
        }

        @Override
        public void save() {
            super.save();
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.refreshDimensions();
            }
        }
    }
}

