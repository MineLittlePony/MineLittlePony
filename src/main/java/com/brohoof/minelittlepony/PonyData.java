package com.brohoof.minelittlepony;

import java.awt.image.BufferedImage;
import java.util.Map;

import com.google.common.collect.ImmutableBiMap;

public class PonyData {

    private static final Map<Integer, PonyRace> RACE_COLORS = ImmutableBiMap.<Integer, PonyRace> builder()
            .put(0xfff9b131, PonyRace.EARTH)
            .put(0xffd19fe4, PonyRace.UNICORN)
            .put(0xff88caf0, PonyRace.PEGASUS)
            .put(0xfffef9fc, PonyRace.ALICORN)
            .put(0xffd0cccf, PonyRace.ZEBRA)
            .put(0xff282b29, PonyRace.CHANGELING)
            .build();
    private static final Map<Integer, TailLengths> TAIL_COLORS = ImmutableBiMap.<Integer, TailLengths> builder()
            .put(0xff425844, TailLengths.STUB)
            .put(0xffd19fe4, TailLengths.QUARTER)
            .put(0xff534b76, TailLengths.HALF)
            .put(0xff8a6b7f, TailLengths.THREE_QUARTERS).build();
    private static final Map<Integer, PonySize> SIZE_COLORS = ImmutableBiMap.<Integer, PonySize> builder()
            .put(0xffffbe53, PonySize.FOAL)
            .put(0xffce3254, PonySize.LARGE)
            .put(0xff534b76, PonySize.PRINCESS)
            .build();

    private PonyRace race = PonyRace.EARTH;
    private TailLengths tailSize = TailLengths.FULL;
    private PonyGender gender = PonyGender.MARE;
    private PonySize size = PonySize.NORMAL;
    private int glowColor = 0x4444aa;

    private int textureWidth;

    private int textureHeight;

    public PonyRace getRace() {
        return race;
    }

    public void setRace(PonyRace race) {
        this.race = race;
    }

    public TailLengths getTail() {
        return tailSize;
    }

    public void setTail(TailLengths tailSize) {
        this.tailSize = tailSize;
    }

    public PonyGender getGender() {
        return gender;
    }

    public void setGender(PonyGender gender) {
        this.gender = gender;
    }

    public PonySize getSize() {
        return size;
    }

    public void setSize(PonySize size) {
        this.size = size;
    }

    public int getGlowColor() {
        return glowColor;
    }

    public void setGlowColor(int glowColor) {
        this.glowColor = glowColor;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public static PonyData parse(BufferedImage image) {
        PonyData data = new PonyData();

        int race = TriggerPixels.RACE.readColor(image);
        data.race = RACE_COLORS.get(race);

        int tail = TriggerPixels.TAIL.readColor(image);
        if (TAIL_COLORS.containsKey(tail))
            data.tailSize = TAIL_COLORS.get(tail);

        int gender = TriggerPixels.GENDER.readColor(image);
        if (gender == 0xffffff)
            data.gender = PonyGender.STALLION;

        int size = TriggerPixels.SIZE.readColor(image);
        if (SIZE_COLORS.containsKey(size))
            data.size = SIZE_COLORS.get(size);

        int color = TriggerPixels.GLOW.readColor(image);
        if (color != 0x000000)
            data.glowColor = color;

        data.textureWidth = image.getWidth();
        data.textureHeight = image.getHeight();

        return data;
    }

    private enum TriggerPixels {
        RACE(0, 0),
        TAIL(0, 1),
        GENDER(0, 2),
        SIZE(0, 3),
        GLOW(1, 0);

        private int x, y;

        private TriggerPixels(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int readColor(BufferedImage image) {
            return image.getRGB(x, y);
        }
    }
}
