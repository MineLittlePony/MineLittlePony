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
    GLOW(null, Channel.RAW, 0, 1),
    WEARABLES(PonyWearable.NONE, Channel.RAW, 1, 1);

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

    public <T extends Enum<T> & ITriggerPixelMapped<T>> boolean[] readFlags(BufferedImage image) {
        boolean[] out = new boolean[def.getClass().getEnumConstants().length];
        readFlags(out, image);
        return out;
    }

    public <T extends Enum<T> & ITriggerPixelMapped<T>> void readFlags(boolean[] out,  BufferedImage image) {
        readFlag(out, Channel.RED, image);
        readFlag(out, Channel.GREEN, image);
        readFlag(out, Channel.BLUE, image);
    }

    private <T extends Enum<T> & ITriggerPixelMapped<T>> void readFlag(boolean[] out, Channel channel, BufferedImage image) {
        T value = ITriggerPixelMapped.getByTriggerPixel((T)def, channel.readValue(x, y, image));
        if (value != def) {
            out[value.ordinal()] = true;
        }
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