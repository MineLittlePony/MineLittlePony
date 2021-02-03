package com.minelittlepony.api.pony.network;

import net.minecraft.network.PacketByteBuf;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.TailLength;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.UUID;

public class MsgPonyData implements IPonyData {

    private final Race race;
    private final TailLength tailLength;
    private final Gender gender;
    private final Size size;
    private final int glowColor;
    private final boolean hasHorn;
    private final boolean hasMagic;

    private final boolean noSkin;

    private final boolean[] wearables;

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
        this.noSkin = noSkin;
    }

    public void toBuffer(PacketByteBuf buffer) {
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

    private static final class MsgSize implements Size {

        private final int ordinal;
        private final String name;
        private final float shadow;
        private final float scale;
        private final float eyeHeight;
        private final float eyeDistance;

        MsgSize(Size size) {
            ordinal = size.ordinal();
            name = size.name();
            shadow = size.getShadowSize();
            scale = size.getScaleFactor();
            eyeHeight = size.getEyeHeightFactor();
            eyeDistance = size.getEyeDistanceFactor();
        }

        MsgSize(PacketByteBuf buffer) {
            ordinal = buffer.readInt();
            name = buffer.readString(32767);
            shadow = buffer.readFloat();
            scale = buffer.readFloat();
            eyeHeight = buffer.readFloat();
            eyeDistance = buffer.readFloat();
        }

        public void toBuffer(PacketByteBuf buffer) {
            buffer.writeInt(ordinal);
            buffer.writeString(name);
            buffer.writeFloat(shadow);
            buffer.writeFloat(scale);
            buffer.writeFloat(eyeHeight);
            buffer.writeFloat(eyeDistance);
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
    }
}
