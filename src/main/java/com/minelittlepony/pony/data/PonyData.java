package com.minelittlepony.pony.data;

import com.google.common.base.MoreObjects;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.anim.BasicEasingInterpolator;
import com.minelittlepony.model.anim.IInterpolator;

import java.awt.image.BufferedImage;
import javax.annotation.concurrent.Immutable;


/**
 * Implementation for IPonyData.
 *
 */
@Immutable
public class PonyData implements IPonyData {
    private final PonyRace race;
    private final TailLengths tailSize;
    private final PonyGender gender;
    private final PonySize size;
    private final int glowColor;

    private final boolean[] wearables;

    private final IInterpolator interpolator = new BasicEasingInterpolator();

    public PonyData() {
        race = PonyRace.HUMAN;
        tailSize = TailLengths.FULL;
        gender = PonyGender.MARE;
        size = PonySize.NORMAL;
        glowColor = 0x4444aa;

        wearables = new boolean[PonyWearable.values().length];
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
    public PonyRace getRace() {
        return race;
    }

    @Override
    public TailLengths getTail() {
        return tailSize;
    }

    @Override
    public PonyGender getGender() {
        return gender;
    }

    @Override
    public PonySize getSize() {
        return MineLittlePony.getConfig().sizes ? size : PonySize.NORMAL;
    }

    @Override
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public boolean hasMagic() {
        return race != null && getRace().hasHorn() && getGlowColor() != 0;
    }

    @Override
    public boolean isWearing(PonyWearable wearable) {
        return wearables[wearable.ordinal()];
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailSize", tailSize)
                .add("gender", gender)
                .add("size", size)
                .add("wearables", PonyWearable.flags(wearables))
                .add("glowColor", "#" + Integer.toHexString(glowColor))
                .toString();
    }

    @Override
    public IInterpolator getInterpolator() {
        return interpolator;
    }

    /**
     * Parses an image buffer into a new IPonyData representing the values stored in it's individual trigger pixels.
     */
    static IPonyData parse(BufferedImage image) {
        return new PonyData(image);
    }
}
