package com.minelittlepony.api.pony.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.TailLength;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.UUID;

public class MsgPonyData implements Channel.Packet, IPonyData {

    private final Race race;
    private final TailLength tailLength;
    private final Gender gender;
    private final Size size;
    private final int glowColor;
    private final boolean hasHorn;
    private final boolean hasMagic;

    private final boolean noSkin;

    MsgPonyData(PacketByteBuf buffer) {
        race = Race.values()[buffer.readInt()];
        tailLength = TailLength.values()[buffer.readInt()];
        gender = Gender.values()[buffer.readInt()];
        size = Size.values()[buffer.readInt()];
        glowColor = buffer.readInt();
        hasHorn = buffer.readBoolean();
        hasMagic = buffer.readBoolean();
        noSkin = buffer.readBoolean();
    }

    public MsgPonyData(IPonyData data, boolean noSkin) {
        race = data.getRace();
        tailLength = data.getTail();
        gender = data.getGender();
        size = data.getSize();
        glowColor = data.getGlowColor();
        hasHorn = data.hasHorn();
        hasMagic = data.hasMagic();
        this.noSkin = noSkin;
    }

    @Override
    public void handle(PacketContext context) {
        PonyDataCallback.EVENT.invoker().onPonyDataAvailable(context.getPlayer(), this, noSkin, EnvType.SERVER);
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeInt(race.ordinal());
        buffer.writeInt(tailLength.ordinal());
        buffer.writeInt(gender.ordinal());
        buffer.writeInt(size.ordinal());
        buffer.writeInt(glowColor);
        buffer.writeBoolean(hasHorn);
        buffer.writeBoolean(hasMagic);
        buffer.writeBoolean(noSkin);
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
    public boolean isWearing(Wearable wearable) {
        return false;
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }
}
