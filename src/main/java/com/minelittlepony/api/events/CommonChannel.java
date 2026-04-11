package com.minelittlepony.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.api.pony.PonyData;

public class CommonChannel {
    private static final Logger LOGGER = LogManager.getLogger("MineLittlePony:Networking");

    public static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, _) -> {
            LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(PonyDataRequest.INSTANCE);
        });

        PayloadTypeRegistry.clientboundPlay().register(PonyDataRequest.ID, PonyDataRequest.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PonyDataPayload.ID, PonyDataPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PonyDataPayload.ID, PonyDataPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PonyDataPayload.ID, (packet, context) -> {
            context.server().execute(() -> {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(context.player(), packet.data(), EnvType.SERVER);
            });
        });
    }

    record PonyDataPayload(PonyData data) implements CustomPacketPayload {
        public static final Type<PonyDataPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("minelittlepony", "pony_data"));
        public static final StreamCodec<FriendlyByteBuf, PonyDataPayload> CODEC = MsgPonyData.STREAM_CODEC.map(MsgPonyData::data, MsgPonyData::new).map(PonyDataPayload::new, PonyDataPayload::data);

        @Override
        public Type<PonyDataPayload> type() {
            return ID;
        }
    }

    record PonyDataRequest() implements CustomPacketPayload {
        public static final PonyDataRequest INSTANCE = new PonyDataRequest();
        public static final Type<PonyDataRequest> ID = new Type<>(Identifier.fromNamespaceAndPath("minelittlepony", "request_pony_data"));
        public static final StreamCodec<FriendlyByteBuf, PonyDataRequest> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<PonyDataRequest> type() {
            return ID;
        }
    }
}
