package com.minelittlepony.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

public interface ClientReadyCallback {

    Event<ClientReadyCallback> EVENT = EventFactory.createArrayBacked(ClientReadyCallback.class, listeners -> client -> {
        for (ClientReadyCallback event : listeners) {
            event.onClientPostInit(client);
        }
    });

    void onClientPostInit(MinecraftClient client);

    class Handler implements ClientTickCallback {

        private static Handler instance;

        private boolean firstTick = true;

        private Handler() {}

        public static void register() {
            // make sure to only register once
            if (instance == null) {
                instance = new Handler();
                ClientTickCallback.EVENT.register(instance);
            }
        }

        @Override
        public void tick(MinecraftClient client) {
            if (firstTick) {
                ClientReadyCallback.EVENT.invoker().onClientPostInit(client);
                firstTick = false;
            }

        }
    }
}
