package com.minelittlepony.api.pony.network;

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
                Flags.read(Wearable.class, buffer)
        );
    }

    public static PacketByteBuf write(PonyData data, PacketByteBuf buffer) {
        buffer.writeShort(API_IDENTIFIER);
        buffer.writeByte(API_VERSION);
        buffer.writeEnumConstant(data.race());
        buffer.writeEnumConstant(data.tailLength());
        buffer.writeEnumConstant(data.tailShape());
        buffer.writeEnumConstant(data.gender());
        new MsgSize(data.size()).toBuffer(buffer);
        buffer.writeInt(data.glowColor());
        buffer.writeBoolean(data.noSkin());
        data.gear().write(buffer);
        return buffer;
    }

    private record MsgSize (
            int ordinal,
            String name,
            float shadowSize,
            float scaleFactor,
            float eyeHeightFactor,
            float eyeDistanceFactor,
            int colorCode) implements Size {

        MsgSize(Size size) {
            this(size.ordinal(), size.name(), size.shadowSize(), size.scaleFactor(), size.eyeHeightFactor(), size.eyeDistanceFactor(), size.colorCode());
        }

        MsgSize(PacketByteBuf buffer) {
            this(buffer.readInt(), buffer.readString(32767), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
        }

        public void toBuffer(PacketByteBuf buffer) {
            buffer.writeInt(ordinal);
            buffer.writeString(name);
            buffer.writeFloat(shadowSize);
            buffer.writeFloat(scaleFactor);
            buffer.writeFloat(eyeHeightFactor);
            buffer.writeFloat(eyeDistanceFactor);
            buffer.writeFloat(colorCode);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
