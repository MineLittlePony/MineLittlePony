package com.minelittlepony;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableBiMap;

import java.awt.image.BufferedImage;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PonyData implements IPonyData {

    private static final Map<Integer, PonyRace> RACE_COLORS = ImmutableBiMap.<Integer, PonyRace>builder()
            .put(0xf9b131, PonyRace.EARTH)
            .put(0xd19fe4, PonyRace.UNICORN)
            .put(0x88caf0, PonyRace.PEGASUS)
            .put(0xfef9fc, PonyRace.ALICORN)
            .put(0xd0cccf, PonyRace.ZEBRA)
            .put(0x282b29, PonyRace.CHANGELING)
            .put(0xcaed5a, PonyRace.REFORMED_CHANGELING)
            .put(0xae9145, PonyRace.GRIFFIN)
            .put(0xd6ddac, PonyRace.HIPPOGRIFF)
            .build();
    private static final Map<Integer, TailLengths> TAIL_COLORS = ImmutableBiMap.<Integer, TailLengths>builder()
            .put(0x425844, TailLengths.STUB)
            .put(0xd19fe4, TailLengths.QUARTER)
            .put(0x534b76, TailLengths.HALF)
            .put(0x8a6b7f, TailLengths.THREE_QUARTERS).build();
    private static final Map<Integer, PonySize> SIZE_COLORS = ImmutableBiMap.<Integer, PonySize>builder()
            .put(0xffbe53, PonySize.FOAL)
            .put(0xce3254, PonySize.LARGE)
            .put(0x534b76, PonySize.TALL)
            .build();

    private final PonyRace race;
    private final TailLengths tailSize;
    private final PonyGender gender;
    private final PonySize size;
    private final int glowColor;

    public PonyData() {
        this(PonyRace.HUMAN, TailLengths.FULL, PonyGender.MARE, PonySize.NORMAL, 0x4444aa);
    }

    private PonyData(PonyRace race, TailLengths tailSize, PonyGender gender, PonySize size, int glowColor) {
        this.race = race;
        this.tailSize = tailSize;
        this.gender = gender;
        this.size = size;
        this.glowColor = glowColor;
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
        int racePx = TriggerPixels.RACE.readColor(image);
        PonyRace race = RACE_COLORS.getOrDefault(racePx, PonyRace.HUMAN);

        int tailPx = TriggerPixels.TAIL.readColor(image);
        TailLengths tail = TAIL_COLORS.getOrDefault(tailPx, TailLengths.FULL);

        int sizePx = TriggerPixels.SIZE.readColor(image);
        PonySize size = SIZE_COLORS.getOrDefault(sizePx, PonySize.NORMAL);

        int genderPx = TriggerPixels.GENDER.readColor(image);
        PonyGender gender = genderPx == 0xffffff ? PonyGender.STALLION : PonyGender.MARE;

        int glowColor = TriggerPixels.GLOW.readColor(image, -1);

        return new PonyData(race, tail, gender, size, glowColor);
    }

    private enum TriggerPixels {
        RACE(0, 0),
        TAIL(1, 0),
        GENDER(2, 0),
        SIZE(3, 0),
        GLOW(0, 1);

        private int x, y;

        TriggerPixels(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int readColor(BufferedImage image) {
            return readColor(image, 0xffffff);
        }

        private int readColor(BufferedImage image, int mask) {
            return image.getRGB(x, y) & mask;
        }
    }
}
