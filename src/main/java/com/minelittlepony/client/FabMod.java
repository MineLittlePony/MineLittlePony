package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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
}
