package com.minelittlepony.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.api.pony.PonyData;

@Environment(EnvType.CLIENT)
public class ClientChannel {
    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony:Networking");

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((_, _) -> {
           registered = false;
        });

        ClientPlayNetworking.registerGlobalReceiver(CommonChannel.PonyDataRequest.ID, (_, _) -> {
            registered = true;
            LOGGER.info("Server has just consented");
        });
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static boolean broadcastPonyData(PonyData packet) {
        if (!isRegistered()) {
            return false;
        }
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Client packet send called by the server");
        }

        ClientPlayNetworking.send(new CommonChannel.PonyDataPayload(packet));
        return true;
    }
}