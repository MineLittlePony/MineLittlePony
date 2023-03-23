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
        race = data.getRace();
        tailLength = data.getTailLength();
        tailShape = data.getTailShape();
        gender = data.getGender();
        size = data.getSize();
        glowColor = data.getGlowColor();
        wearables = Wearable.flags(data.getGear());
        wearableColor = data.getTriggerPixels().get("gear").getColorCode();
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

        Wearable[] gear = getGear();
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
    public Race getRace() {
        return race;
    }

    @Override
    public TailLength getTailLength() {
        return tailLength;
    }

    @Override
    public TailShape getTailShape() {
        return tailShape;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public Wearable[] getGear() {
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
    public Map<String, TriggerPixelType<?>> getTriggerPixels() {
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
                .add("wearables", getGear())
                .add("glowColor", TriggerPixelType.toHex(glowColor))
                .toString();
    }

    private static final class MsgSize implements Size {

        private final int ordinal;
        private final String name;
        private final float shadow;
        private final float scale;
        private final float eyeHeight;
        private final float eyeDistance;
        private final int triggerPixel;

        MsgSize(Size size) {
            ordinal = size.ordinal();
            name = size.name();
            shadow = size.getShadowSize();
            scale = size.getScaleFactor();
            eyeHeight = size.getEyeHeightFactor();
            eyeDistance = size.getEyeDistanceFactor();
            triggerPixel = size.getColorCode();
        }

        MsgSize(PacketByteBuf buffer) {
            ordinal = buffer.readInt();
            name = buffer.readString(32767);
            shadow = buffer.readFloat();
            scale = buffer.readFloat();
            eyeHeight = buffer.readFloat();
            eyeDistance = buffer.readFloat();
            triggerPixel = buffer.readInt();
        }

        public void toBuffer(PacketByteBuf buffer) {
            buffer.writeInt(ordinal);
            buffer.writeString(name);
            buffer.writeFloat(shadow);
            buffer.writeFloat(scale);
            buffer.writeFloat(eyeHeight);
            buffer.writeFloat(eyeDistance);
            buffer.writeFloat(triggerPixel);
        }

        @Override
        public int ordinal() {
            return ordinal;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public float getShadowSize() {
            return shadow;
        }

        @Override
        public float getScaleFactor() {
            return scale;
        }

        @Override
        public float getEyeHeightFactor() {
            return eyeHeight;
        }

        @Override
        public float getEyeDistanceFactor() {
            return eyeDistance;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getColorCode() {
            return triggerPixel;
        }
    }
}
