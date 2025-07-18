package com.minelittlepony.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.api.pony.PonyData;

public class CommonChannel {
    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony:Networking");

    public static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(PonyDataRequest.INSTANCE);
        });

        PayloadTypeRegistry.playS2C().register(PonyDataRequest.ID, PonyDataRequest.CODEC);
        PayloadTypeRegistry.playS2C().register(PonyDataPayload.ID, PonyDataPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PonyDataPayload.ID, PonyDataPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PonyDataPayload.ID, (packet, context) -> {
            context.server().execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(context.player(), packet.data(), EnvType.SERVER);
            });
        });
    }

    record PonyDataPayload(PonyData data) implements CustomPayload {
        public static final Id<PonyDataPayload> ID = new Id<>(Identifier.of("minelittlepony", "pony_data"));
        public static final PacketCodec<PacketByteBuf, PonyDataPayload> CODEC = CustomPayload.codecOf(
                (p, buffer) -> MsgPonyData.write(p.data(), buffer),
                buffer -> new PonyDataPayload(MsgPonyData.read(buffer))
        );

        @Override
        public Id<PonyDataPayload> getId() {
            return ID;
        }
    }

    record PonyDataRequest() implements CustomPayload {
        public static final PonyDataRequest INSTANCE = new PonyDataRequest();
        public static final Id<PonyDataRequest> ID = new Id<>(Identifier.of("minelittlepony", "request_pony_data"));
        public static final PacketCodec<PacketByteBuf, PonyDataRequest> CODEC = PacketCodec.unit(INSTANCE);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
