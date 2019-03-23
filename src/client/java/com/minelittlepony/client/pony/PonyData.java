package com.minelittlepony.client.pony;

import net.minecraft.client.resources.data.IMetadataSection;

import com.google.common.base.MoreObjects;
import com.minelittlepony.common.pony.IPonyData;
import com.minelittlepony.common.pony.meta.Gender;
import com.minelittlepony.common.pony.meta.Race;
import com.minelittlepony.common.pony.meta.Wearable;
import com.minelittlepony.common.pony.meta.Size;
import com.minelittlepony.common.pony.meta.TailLength;
import com.minelittlepony.common.pony.meta.TriggerPixels;
import com.minelittlepony.util.animation.BasicEasingInterpolator;
import com.minelittlepony.util.animation.IInterpolator;

import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;


/**
 * Implementation for IPonyData.
 *
 */
@Immutable
public class PonyData implements IPonyData, IMetadataSection {
    private final Race race;
    private final TailLength tailSize;
    private final Gender gender;
    private final Size size;
    private final int glowColor;

    private final boolean[] wearables;

    public PonyData() {
        race = Race.HUMAN;
        tailSize = TailLength.FULL;
        gender = Gender.MARE;
        size = Size.NORMAL;
        glowColor = 0x4444aa;

        wearables = new boolean[Wearable.values().length];
    }

    private PonyData(BufferedImage image) {
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
    public static IPonyData parse(BufferedImage image) {
        return new PonyData(image);
    }
}
