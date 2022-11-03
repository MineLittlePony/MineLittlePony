package com.minelittlepony.api.pony.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

import com.google.common.base.Suppliers;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.TailLength;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

public class MsgPonyData implements IPonyData {

    private final Race race;
    private final TailLength tailLength;
    private final Gender gender;
    private final Size size;
    private final int glowColor;
    private final boolean hasHorn;
    private final boolean hasMagic;

    private final boolean noSkin;

    private final int wearableColor;
    private final boolean[] wearables;

    private final Supplier<Map<String, TriggerPixelType<?>>> triggerPixels = Suppliers.memoize(() -> Util.make(new TreeMap<>(), this::initTriggerPixels));
    private void initTriggerPixels(Map<String, TriggerPixelType<?>> map) {
        map.put("race", race);
        map.put("tail", tailLength);
        map.put("gender", gender);
        map.put("size", size);
        map.put("magic", TriggerPixelType.of(glowColor));
        map.put("gear", TriggerPixelType.of(wearableColor));
    }

    public MsgPonyData(PacketByteBuf buffer) {
        race = Race.values()[buffer.readInt()];
        tailLength = TailLength.values()[buffer.readInt()];
        gender = Gender.values()[buffer.readInt()];
        size = new MsgSize(buffer);
        glowColor = buffer.readInt();
        hasHorn = buffer.readBoolean();
        hasMagic = buffer.readBoolean();
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
        tailLength = data.getTail();
        gender = data.getGender();
        size = data.getSize();
        glowColor = data.getGlowColor();
        hasHorn = data.hasHorn();
        hasMagic = data.hasMagic();
        wearables = Wearable.flags(data.getGear());
        wearableColor = data.getTriggerPixels().get("gear").getColorCode();
        this.noSkin = noSkin;
    }

    public PacketByteBuf toBuffer(PacketByteBuf buffer) {
        buffer.writeInt(race.ordinal());
        buffer.writeInt(tailLength.ordinal());
        buffer.writeInt(gender.ordinal());
        new MsgSize(size).toBuffer(buffer);
        buffer.writeInt(glowColor);
        buffer.writeBoolean(hasHorn);
        buffer.writeBoolean(hasMagic);
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
    public TailLength getTail() {
        return tailLength;
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
    public boolean hasHorn() {
        return hasHorn;
    }

    @Override
    public boolean hasMagic() {
        return hasMagic;
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
