package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.gui.hdskins.MineLPHDSkins;
import com.minelittlepony.common.client.IModUtilities;

import javax.annotation.Nullable;

public class FabMod implements ClientModInitializer, ClientTickCallback, IModUtilities {

    @Nullable
    private MineLPClient mlp;

    private boolean firstTick = true;

    @Override
    public void onInitializeClient() {
        ClientTickCallback.EVENT.register(this);

        if (FabricLoader.getInstance().isModLoaded("hdskins")) {
            mlp = new MineLPHDSkins(this);
        } else {
            mlp = new MineLPClient(this);
        }
    }

    @Override
    public void tick(MinecraftClient client) {
        if (mlp == null) {
            return;
        }

        if (firstTick) {
            firstTick = false;

            mlp.postInit(client);
        } else {
            mlp.onTick(client, client.world != null && client.player != null);
        }
    }

    @Override
    public KeyBinding registerKeybind(String category, int key, String bindName) {
        // normalize Fabric's behavior
        if (bindName.startsWith("key.")) {
            bindName = bindName.replace("key.", "");
        }

        FabricKeyBinding binding = FabricKeyBinding.Builder.create(new Identifier(bindName) {
            @Override
            public String toString() { return getPath(); }
        }, InputUtil.Type.KEYSYM, key, category).build();

        KeyBindingRegistry.INSTANCE.register(binding);
        return binding;
    }
}
