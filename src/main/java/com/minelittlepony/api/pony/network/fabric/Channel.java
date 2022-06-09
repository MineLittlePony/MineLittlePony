package com.minelittlepony.api.pony.network.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.client.MineLittlePony;

@Environment(EnvType.CLIENT)
public class Channel {
    private static final Identifier CLIENT_PONY_DATA = new Identifier("minelittlepony", "pony_data");
    private static final Identifier REQUEST_PONY_DATA = new Identifier("minelittlepony", "request_pony_data");

    private static boolean registered;

    public static void bootstrap() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
           registered = false;
           MineLittlePony.logger.info("Resetting registered flag");
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            MineLittlePony.logger.info("Sending consent packet to " + handler.getPlayer().getName().getString());

            sender.sendPacket(REQUEST_PONY_DATA, PacketByteBufs.empty());
        });

        ClientPlayNetworking.registerGlobalReceiver(REQUEST_PONY_DATA, (client, handler, ignored, sender) -> {
            if (client.player != null) {
                IPony pony = MineLittlePony.getInstance().getManager().getPony(client.player);
                registered = true;
                MineLittlePony.logger.info("Server has just consented");

                sender.sendPacket(CLIENT_PONY_DATA, new MsgPonyData(pony.getMetadata(), pony.isDefault()).toBuffer(PacketByteBufs.create()));
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_PONY_DATA, (server, player, ignore, buffer, ignore2) -> {
            MsgPonyData packet = new MsgPonyData(buffer);
            server.execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, packet.isNoSkin(), EnvType.SERVER);
            });
        });
    }

    public static void broadcastPonyData(MsgPonyData packet) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Client packet send called by the server");
        }

        if (!registered) {
            if (MinecraftClient.getInstance().isInSingleplayer() || MinecraftClient.getInstance().isIntegratedServerRunning()) {
                MineLittlePony.logger.info("Sending pony skin data over as we are either in single-player or lan");
            } else {
                MineLittlePony.logger.info("Skipping network packet as the server has not consented");
                return;
            }
        }

        ClientPlayNetworking.send(CLIENT_PONY_DATA, packet.toBuffer(PacketByteBufs.create()));
    }
}