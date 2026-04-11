package com.minelittlepony.server;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.events.CommonChannel;

import java.lang.ref.WeakReference;

public class MineLittlePonyServer implements ModInitializer {

    private static WeakReference<MinecraftServer> server = new WeakReference<>(null);

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath("minelittlepony", name);
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server.get();
    }

    @Override
    public void onInitialize() {
        CommonChannel.bootstrap();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            MineLittlePonyServer.server = new WeakReference<>(server);
        });
    }
}
