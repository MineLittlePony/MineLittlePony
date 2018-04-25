package com.minelittlepony.pony.data;

import com.google.common.base.MoreObjects;
import com.minelittlepony.MineLittlePony;

import java.awt.image.BufferedImage;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PonyData implements IPonyData {
    private final PonyRace race;
    private final TailLengths tailSize;
    private final PonyGender gender;
    private final PonySize size;
    private final int glowColor;

    public PonyData() {
        this.race = PonyRace.HUMAN;
        this.tailSize = TailLengths.FULL;
        this.gender = PonyGender.MARE;
        this.size = PonySize.NORMAL;
        this.glowColor = 0x4444aa;
    }
    
    private PonyData(BufferedImage image) {
        this.race = TriggerPixels.RACE.readValue(image);
        this.tailSize = TriggerPixels.TAIL.readValue(image);
        this.size = TriggerPixels.SIZE.readValue(image);
        this.gender = TriggerPixels.GENDER.readValue(image);
        this.glowColor = TriggerPixels.GLOW.readColor(image, -1);
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
        return this.race != null && this.race.hasHorn() && this.glowColor != 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailSize", tailSize)
                .add("gender", gender)
                .add("size", size)
                .add("glowColor", "#" + Integer.toHexString(glowColor))
                .toString();
    }

    static IPonyData parse(BufferedImage image) {
        return new PonyData(image);
    }
}
