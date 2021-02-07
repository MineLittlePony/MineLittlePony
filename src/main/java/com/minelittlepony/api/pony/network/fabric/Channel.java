package com.minelittlepony.api.pony.network.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.network.MsgPonyData;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface Channel {
    Consumer<MsgPonyData> CLIENT_PONY_DATA = clientToServer(new Identifier("minelittlepony", "pony_data"), MsgPonyData::new, MsgPonyData::toBuffer, (packet, player) -> {
        PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, packet, packet.isNoSkin(), EnvType.SERVER);
    });

    static void bootstrap() { }

    static <T> Consumer<T> clientToServer(Identifier id, Function<PacketByteBuf, T> factory,
            BiConsumer<T, PacketByteBuf> bufferWriter,
            BiConsumer<T, PlayerEntity> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, hhandler, buffer, responseSender) -> {
            T packet = factory.apply(buffer);
            server.execute(() -> {
                handler.accept(packet, player);
            });
        });
        return packet -> {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw new RuntimeException("Client packet send called by the server");
            }

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            bufferWriter.accept(packet, buf);

            ClientPlayNetworking.send(id, buf);
        };
    }
}