package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;

import com.minelittlepony.client.gui.hdskins.IndirectHDSkins;

public class FabMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MineLPClient mlp = new MineLPClient();
        ClientReadyCallback.Handler.register();
        ClientTickCallback.EVENT.register(mlp::onTick);
        ClientReadyCallback.EVENT.register(mlp::postInit);

        if (FabricLoader.getInstance().isModLoaded("hdskins")) {
            IndirectHDSkins.initialize();
        }
    }
}
