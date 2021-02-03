package com.minelittlepony.api.pony.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public interface Channel {

    @Environment(EnvType.CLIENT)
    SPacketType<MsgPonyData> CLIENT_PONY_DATA = clientToServer(new Identifier("minelittlepony", "pony_data"), MsgPonyData::new);

    static void bootstrap() { }

    static <T extends Packet> SPacketType<T> clientToServer(Identifier id, Function<PacketByteBuf, T> factory) {
        ServerSidePacketRegistry.INSTANCE.register(id, (context, buffer) -> factory.apply(buffer).handleOnMain(context));
        return () -> id;
    }

    interface SPacketType<T extends Packet> {
        Identifier getId();

        default void send(T packet) {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw new RuntimeException("Client packet send called by the server");
            }
            ClientSidePacketRegistry.INSTANCE.sendToServer(getId(), packet.toBuffer());
        }

        default net.minecraft.network.Packet<?> toPacket(T packet) {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw new RuntimeException("Client packet send called by the server");
            }
            return ClientSidePacketRegistry.INSTANCE.toPacket(getId(), packet.toBuffer());
        }
    }

    interface Packet {
        void handle(PacketContext context);

        void toBuffer(PacketByteBuf buffer);

        default void handleOnMain(PacketContext context) {
            context.getTaskQueue().execute(() -> handle(context));
        }

        default PacketByteBuf toBuffer() {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            toBuffer(buf);
            return buf;
        }
    }
}