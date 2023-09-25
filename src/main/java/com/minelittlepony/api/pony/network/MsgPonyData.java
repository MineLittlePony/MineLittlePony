package com.minelittlepony.api.pony.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Suppliers;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

public class MsgPonyData implements IPonyData {
    private static final short API_IDENTIFIER = (short) 0xABCD;
    // API version - increment this number before any time any data is added/removed/moved in the data stream
    private static final byte API_VERSION = 2;

    private final Race race;
    private final TailLength tailLength;
    private final TailShape tailShape;
    private final Gender gender;
    private final Size size;
    private final int glowColor;

    private final boolean noSkin;

    private final int wearableColor;
    private final boolean[] wearables;

    private final Supplier<Map<String, TriggerPixelType<?>>> triggerPixels = Suppliers.memoize(() -> Util.make(new TreeMap<>(), this::initTriggerPixels));
    private void initTriggerPixels(Map<String, TriggerPixelType<?>> map) {
        map.put("race", race);
        map.put("tailLength", tailLength);
        map.put("tailShape", tailShape);
        map.put("gender", gender);
        map.put("size", size);
        map.put("magic", TriggerPixelType.of(glowColor));
        map.put("gear", TriggerPixelType.of(wearableColor));
    }

    public MsgPonyData(PacketByteBuf buffer) {
        short data = buffer.readShort();
        if (data != API_IDENTIFIER || buffer.readByte() != API_VERSION) {
            race = null;
            tailLength = null;
            tailShape = null;
            gender = null;
            size = null;
            glowColor = 0;
            noSkin = true;
            wearables = null;
            wearableColor = 0;
            return;
        }
        race = buffer.readEnumConstant(Race.class);
        tailLength = buffer.readEnumConstant(TailLength.class);
        tailShape = buffer.readEnumConstant(TailShape.class);
        gender = buffer.readEnumConstant(Gender.class);
        size = new MsgSize(buffer);
        glowColor = buffer.readInt();
        noSkin = buffer.readBoolean();
        Wearable[] gear = new Wearable[buffer.readInt()];
        Wearable[] all = Wearable.values();
        for (int i = 0; i < gear.length; i++) {
            gear[i] = all[buffer.readInt()];
        }
        wearables = Wearable.flags(gear);
        wearableColor = buffer.readInt();
    }

    public MsgPonyData(IPonyData data, boolean noSkin) {
        race = data.race();
        tailLength = data.tailLength();
        tailShape = data.tailShape();
        gender = data.gender();
        size = data.size();
        glowColor = data.glowColor();
        wearables = Wearable.flags(data.gear());
        wearableColor = data.attributes().get("gear").colorCode();
        this.noSkin = noSkin;
    }

    public PacketByteBuf toBuffer(PacketByteBuf buffer) {
        buffer.writeShort(API_IDENTIFIER);
        buffer.writeByte(API_VERSION);
        buffer.writeEnumConstant(race);
        buffer.writeEnumConstant(tailLength);
        buffer.writeEnumConstant(tailShape);
        buffer.writeEnumConstant(gender);
        new MsgSize(size).toBuffer(buffer);
        buffer.writeInt(glowColor);
        buffer.writeBoolean(noSkin);

        Wearable[] gear = gear();
        buffer.writeInt(gear.length);
        for (int i = 0; i < gear.length; i++) {
            buffer.writeInt(gear[i].ordinal());
        }
        buffer.writeInt(wearableColor);
        return buffer;
    }

    public boolean isNoSkin() {
        return noSkin;
    }

    @Override
    public Race race() {
        return race;
    }

    @Override
    public TailLength tailLength() {
        return tailLength;
    }

    @Override
    public TailShape tailShape() {
        return tailShape;
    }

    @Override
    public Gender gender() {
        return gender;
    }

    @Override
    public Size size() {
        return size;
    }

    @Override
    public int glowColor() {
        return glowColor;
    }

    @Override
    public Wearable[] gear() {
        return Wearable.flags(wearables);
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables[wearable.ordinal()];
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    @Override
    public Map<String, TriggerPixelType<?>> attributes() {
        return triggerPixels.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailLength", tailLength)
                .add("tailShape", tailShape)
                .add("gender", gender)
                .add("size", size)
                .add("wearables", gear())
                .add("glowColor", TriggerPixelType.toHex(glowColor))
                .toString();
    }

    private record MsgSize (
            int ordinal,
            String name,
            float shadowSize,
            float scaleFactor,
            float eyeHeightFactor,
            float eyeDistanceFactor,
            int triggerPixel) implements Size {

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
            buffer.writeFloat(triggerPixel);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int colorCode() {
            return triggerPixel;
        }
    }
}
