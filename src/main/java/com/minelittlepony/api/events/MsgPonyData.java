package com.minelittlepony.api.events;

import net.minecraft.network.PacketByteBuf;

import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.*;

public class MsgPonyData {
    private static final short API_IDENTIFIER = (short) 0xABCD;
    // API version - increment this number before any time any data is added/removed/moved in the data stream
    private static final byte API_VERSION = 3;

    public static PonyData read(PacketByteBuf buffer) {
        short data = buffer.readShort();
        if (data != API_IDENTIFIER || buffer.readByte() != API_VERSION) {
            return PonyData.NULL;
        }
        return new PonyData(
                buffer.readEnumConstant(Race.class),
                buffer.readEnumConstant(TailLength.class),
                buffer.readEnumConstant(TailShape.class),
                buffer.readEnumConstant(Gender.class),
                new MsgSize(buffer),
                buffer.readInt(),
                buffer.readBoolean(),
                buffer.readVarInt(),
                Flags.read(Wearable.NONE, buffer)
        );
    }

    public static PacketByteBuf write(PonyData data, PacketByteBuf buffer) {
        buffer.writeShort(API_IDENTIFIER);
        buffer.writeByte(API_VERSION);
        buffer.writeEnumConstant(data.race());
        buffer.writeEnumConstant(data.tailLength());
        buffer.writeEnumConstant(data.tailShape());
        buffer.writeEnumConstant(data.gender());
        write(data.size(), buffer);
        buffer.writeInt(data.glowColor());
        buffer.writeBoolean(data.noSkin());
        buffer.writeVarInt(data.priority());
        data.gear().write(buffer);
        return buffer;
    }

    private static void write(Size size, PacketByteBuf buffer) {
        buffer.writeInt(size.ordinal());
        buffer.writeString(size.name());
        buffer.writeFloat(size.shadowSize());
        buffer.writeFloat(size.scaleFactor());
        buffer.writeFloat(size.eyeHeightFactor());
        buffer.writeFloat(size.eyeDistanceFactor());
        buffer.writeFloat(size.colorCode());
    }

    private record MsgSize (
            int ordinal,
            String name,
            float shadowSize,
            float scaleFactor,
            float eyeHeightFactor,
            float eyeDistanceFactor,
            int colorCode) implements Size {
        MsgSize(PacketByteBuf buffer) {
            this(buffer.readInt(), buffer.readString(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
