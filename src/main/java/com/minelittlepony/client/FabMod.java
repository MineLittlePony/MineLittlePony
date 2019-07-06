package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.gui.GuiPonySettings;
import com.minelittlepony.client.hdskins.IndirectHDSkins;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.common.client.gui.style.Style;
import com.minelittlepony.common.event.ScreenInitCallback;

public class FabMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MineLPClient mlp = new MineLPClient();
        ClientReadyCallback.Handler.register();
        ClientTickCallback.EVENT.register(mlp::onTick);
        ClientReadyCallback.EVENT.register(mlp::postInit);
        ScreenInitCallback.EVENT.register(this::onScreenInit);

        if (FabricLoader.getInstance().isModLoaded("hdskins")) {
            IndirectHDSkins.initialize();
        }
    }

    private void onScreenInit(Screen screen, ScreenInitCallback.ButtonList buttons) {
        if (screen instanceof TitleScreen) {
            int y = FabricLoader.getInstance().isModLoaded("hdskins") ? 80 : 50;

            buttons.add(new Button(screen.width - 50, screen.height - y, 20, 20).onClick(sender -> {
                MinecraftClient.getInstance().openScreen(new GuiPonySettings());
            }).setStyle(new Style()
                    .setIcon(new TextureSprite()
                    .setPosition(2, 2)
                    .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                    .setTextureSize(16, 16)
                    .setSize(16, 16))
            ));
        }
    }
}
