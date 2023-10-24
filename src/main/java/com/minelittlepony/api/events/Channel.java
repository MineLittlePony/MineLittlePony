package com.minelittlepony.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;

@Environment(EnvType.CLIENT)
public class Channel {
    private static final Identifier CLIENT_PONY_DATA = new Identifier("minelittlepony", "pony_data");
    private static final Identifier REQUEST_PONY_DATA = new Identifier("minelittlepony", "request_pony_data");

    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony");

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
           registered = false;
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());

            sender.sendPacket(REQUEST_PONY_DATA, PacketByteBufs.empty());
        });

        ClientPlayNetworking.registerGlobalReceiver(REQUEST_PONY_DATA, (client, handler, ignored, sender) -> {
            registered = true;
            if (client.player != null) {
                Pony pony = Pony.getManager().getPony(client.player);
                LOGGER.info("Server has just consented");

                sender.sendPacket(CLIENT_PONY_DATA, MsgPonyData.write(pony.metadata(), PacketByteBufs.create()));
            } else {
                LOGGER.info("Server has just consented but the client player was not set");
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_PONY_DATA, (server, player, ignore, buffer, ignore2) -> {
            PonyData packet = MsgPonyData.read(buffer);
            server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, EnvType.SERVER);
            });
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

        ClientPlayNetworking.send(CLIENT_PONY_DATA, MsgPonyData.write(packet, PacketByteBufs.create()));
        return true;
    }
}