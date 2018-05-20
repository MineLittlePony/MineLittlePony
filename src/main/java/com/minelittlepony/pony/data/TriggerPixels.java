package com.minelittlepony.pony.data;

import java.awt.image.BufferedImage;

/**
 * Individual trigger pixels for a pony skin.
 *
 */
@SuppressWarnings("unchecked")
public enum TriggerPixels {
    RACE(PonyRace.HUMAN, 0, 0),
    TAIL(TailLengths.FULL, 1, 0),
    GENDER(PonyGender.MARE, 2, 0),
    SIZE(PonySize.LARGE, 3, 0),
    GLOW(null, 0, 1);

    private int x;
    private int y;

    ITriggerPixelMapped<?> def;

    TriggerPixels(ITriggerPixelMapped<?> def, int x, int y) {
        this.def = def;
        this.x = x;
        this.y = y;
    }

    /**
     * Reads this trigger pixel's value and returns the raw colour.
     *
     * @param image Image to read
     * @param mask  Colour mask (0xffffff for rgb, -1 for rgba)
     */
    public int readColor(BufferedImage image, int mask) {
        return image.getRGB(x, y) & mask;
    }

    /**
     * Reads this trigger pixel's value and parses it to an Enum instance.
     *
     * @param image Image to read
     */
    public <T extends Enum<T> & ITriggerPixelMapped<T>> T readValue(BufferedImage image) {
        return ITriggerPixelMapped.getByTriggerPixel((T)def, readColor(image, 0xffffff));
    }
}