package com.minelittlepony.pony.data;

import com.google.common.base.MoreObjects;
import com.minelittlepony.MineLittlePony;

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
    private final PonyAccessory accessory;

    public PonyData() {
        race = PonyRace.HUMAN;
        tailSize = TailLengths.FULL;
        gender = PonyGender.MARE;
        size = PonySize.NORMAL;
        glowColor = 0x4444aa;
        accessory = PonyAccessory.NONE;
    }

    private PonyData(BufferedImage image) {
        race = TriggerPixels.RACE.readValue(image);
        tailSize = TriggerPixels.TAIL.readValue(image);
        size = TriggerPixels.SIZE.readValue(image);
        gender = TriggerPixels.GENDER.readValue(image);
        glowColor = TriggerPixels.GLOW.readColor(image, -1);
        accessory = TriggerPixels.ACCESSORY.readValue(image);
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

    public boolean hasAccessory() {
        return accessory != PonyAccessory.NONE;
    }

    public boolean hasBags() {
        return accessory == PonyAccessory.SADDLEBAGS;
    }

    @Override
    public boolean hasMagic() {
        return race != null && race.hasHorn() && glowColor != 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailSize", tailSize)
                .add("gender", gender)
                .add("size", size)
                .add("glowColor", "#" + Integer.toHexString(glowColor))
                .add("accessory", accessory)
                .toString();
    }

    /**
     * Parses an image buffer into a new IPonyData representing the values stored in it's individual trigger pixels.
     */
    static IPonyData parse(BufferedImage image) {
        return new PonyData(image);
    }
}
