package com.minelittlepony.api.pony.network.fabric;

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

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.client.MineLittlePony;

@Environment(EnvType.CLIENT)
public class Channel {
    private static final Identifier CLIENT_PONY_DATA = new Identifier("minelittlepony", "pony_data");
    private static final Identifier REQUEST_PONY_DATA = new Identifier("minelittlepony", "request_pony_data");
    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony:Networking");

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
           registered = false;
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MineLittlePony.logger.info("Sending consent packet to " + handler.getPlayer().getName().getString());

            sender.sendPacket(REQUEST_PONY_DATA, PacketByteBufs.empty());
        });

        ClientPlayNetworking.registerGlobalReceiver(REQUEST_PONY_DATA, (client, handler, ignored, sender) -> {
            registered = true;
            if (client.player != null) {
                IPony pony = IPony.getManager().getPony(client.player);
                LOGGER.info("Server has just consented");

                sender.sendPacket(CLIENT_PONY_DATA, new MsgPonyData(pony.metadata(), pony.defaulted()).toBuffer(PacketByteBufs.create()));
            } else {
                LOGGER.info("Server has just consented but the client player was not set");
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_PONY_DATA, (server, player, ignore, buffer, ignore2) -> {
            MsgPonyData packet = new MsgPonyData(buffer);
            server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, packet.isNoSkin(), EnvType.SERVER);
            });
        });
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static boolean broadcastPonyData(IPonyData packet, boolean noSkin) {
        if (!isRegistered()) {
            return false;
        }
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Client packet send called by the server");
        }

        LOGGER.info("Sending pony data to server for player");
        ClientPlayNetworking.send(CLIENT_PONY_DATA, new MsgPonyData(packet, noSkin).toBuffer(PacketByteBufs.create()));
        return true;
    }
}