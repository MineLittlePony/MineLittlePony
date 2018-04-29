package com.minelittlepony.pony.data;

import java.awt.image.BufferedImage;

/**
 * Individual trigger pixels for a pony skin.
 *
 */
@SuppressWarnings("unchecked")
public enum TriggerPixels {
    RACE(PonyRace.HUMAN, Channel.ALL, 0, 0),
    TAIL(TailLengths.FULL, Channel.ALL, 1, 0),
    GENDER(PonyGender.MARE, Channel.ALL, 2, 0),
    SIZE(PonySize.NORMAL, Channel.ALL, 3, 0),
    GLOW(null, Channel.RAW, 0, 1);

    private int x;
    private int y;

    private Channel channel;

    ITriggerPixelMapped<?> def;

    TriggerPixels(ITriggerPixelMapped<?> def, Channel channel, int x, int y) {
        this.def = def;
        this.channel = channel;
        this.x = x;
        this.y = y;
    }

    /**
     * Reads this trigger pixel's value and returns the raw colour.
     *
     * @param image Image to read
     */
    public int readColor(BufferedImage image) {
        return channel.readValue(x, y, image);
    }

    /**
     * Reads this trigger pixel's value and parses it to an Enum instance.
     *
     * @param image Image to read
     */
    public <T extends Enum<T> & ITriggerPixelMapped<T>> T readValue(BufferedImage image) {
        return ITriggerPixelMapped.getByTriggerPixel((T)def, readColor(image));
    }

    enum Channel {
        RAW(-1, 0),
        ALL(0xffffff, 0),
        RED(0xff0000, 16),
        GREEN(0x00ff00, 8),
        BLUE(0x0000ff, 0);

        private int mask;
        private int offset;

        Channel(int mask, int offset) {
            this.mask = mask;
            this.offset = offset;
        }

        public int readValue(int x, int y, BufferedImage image) {
            return (image.getRGB(x, y) & mask) >> offset;
        }
    }
}