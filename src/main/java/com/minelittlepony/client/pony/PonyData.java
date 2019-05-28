package com.minelittlepony.client.pony;

import net.minecraft.client.texture.NativeImage;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.IPonyData;
import com.minelittlepony.pony.meta.Gender;
import com.minelittlepony.pony.meta.Race;
import com.minelittlepony.pony.meta.Size;
import com.minelittlepony.pony.meta.TailLength;
import com.minelittlepony.pony.meta.TriggerPixels;
import com.minelittlepony.pony.meta.Wearable;
import com.minelittlepony.util.animation.BasicEasingInterpolator;
import com.minelittlepony.util.animation.IInterpolator;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

/**
 * Implementation for IPonyData.
 *
 */
@Immutable
public class PonyData implements IPonyData {

    public static final PonyDataSerialiser SERIALISER = new PonyDataSerialiser();

    @Expose
    private final Race race;

    @Expose
    private final TailLength tailSize;

    @Expose
    private final Gender gender;

    @Expose
    private final Size size;

    @Expose
    private final int glowColor;

    @Expose
    private final boolean[] wearables;

    public PonyData() {
        race = Race.HUMAN;
        tailSize = TailLength.FULL;
        gender = Gender.MARE;
        size = Size.NORMAL;
        glowColor = 0x4444aa;

        wearables = new boolean[Wearable.values().length];
    }

    private PonyData(NativeImage image) {
        race = TriggerPixels.RACE.readValue(image);
        tailSize = TriggerPixels.TAIL.readValue(image);
        size = TriggerPixels.SIZE.readValue(image);
        gender = TriggerPixels.GENDER.readValue(image);
        glowColor = TriggerPixels.GLOW.readColor(image);

        wearables = TriggerPixels.WEARABLES.readFlags(image);
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public TailLength getTail() {
        return tailSize;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Size getSize() {
        return size.getEffectiveSize();
    }

    @Override
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public boolean hasMagic() {
        return getRace() != null && getRace().getEffectiveRace(false).hasHorn() && getGlowColor() != 0;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables[wearable.ordinal()];
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailSize", tailSize)
                .add("gender", gender)
                .add("size", size)
                .add("wearables", Wearable.flags(wearables))
                .add("glowColor", "#" + Integer.toHexString(glowColor))
                .toString();
    }

    @Override
    public IInterpolator getInterpolator(UUID interpolatorId) {
        return BasicEasingInterpolator.getInstance(interpolatorId);
    }

    /**
     * Parses an image buffer into a new IPonyData representing the values stored in it's individual trigger pixels.
     */
    public static IPonyData parse(NativeImage image) {
        return new PonyData(image);
    }
}
