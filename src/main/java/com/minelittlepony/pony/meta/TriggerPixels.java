package com.minelittlepony.pony.meta;

import net.minecraft.client.texture.NativeImage;

import com.minelittlepony.pony.ITriggerPixelMapped;

/**
 * Individual trigger pixels for a pony skin.
 *
 */
@SuppressWarnings("unchecked")
public enum TriggerPixels {
    RACE(Race.HUMAN, Channel.ALL, 0, 0),
    TAIL(TailLength.FULL, Channel.ALL, 1, 0),
    GENDER(Gender.MARE, Channel.ALL, 2, 0),
    SIZE(Size.NORMAL, Channel.ALL, 3, 0),
    GLOW(null, Channel.RAW, 0, 1),
    WEARABLES(Wearable.NONE, Channel.RAW, 1, 1);

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
    public int readColor(NativeImage image) {
        return channel.readValue(x, y, image);
    }

    /**
     * Reads this trigger pixel's value and parses it to an Enum instance.
     *
     * @param image Image to read
     */
    public <T extends Enum<T> & ITriggerPixelMapped<T>> T readValue(NativeImage image) {
        if (Channel.ALPHA.readValue(x, y, image) < 255) {
            return (T)def;
        }

        return ITriggerPixelMapped.getByTriggerPixel((T)def, readColor(image));
    }

    public <T extends Enum<T> & ITriggerPixelMapped<T>> boolean[] readFlags(NativeImage image) {
        boolean[] out = new boolean[def.getClass().getEnumConstants().length];
        readFlags(out, image);
        return out;
    }

    public <T extends Enum<T> & ITriggerPixelMapped<T>> void readFlags(boolean[] out, NativeImage image) {
        readFlag(out, Channel.RED, image);
        readFlag(out, Channel.GREEN, image);
        readFlag(out, Channel.BLUE, image);
    }

    private <T extends Enum<T> & ITriggerPixelMapped<T>> void readFlag(boolean[] out, Channel channel, NativeImage image) {

        if (Channel.ALPHA.readValue(x, y, image) < 255) {
            return;
        }

        T value = ITriggerPixelMapped.getByTriggerPixel((T)def, channel.readValue(x, y, image));

        out[value.ordinal()] |= value != def;
    }

    enum Channel {
        RAW(-1, 0),
        ALL  (0xffffff, 0),
        ALPHA(0xff, 24),
        RED  (0xff, 16),
        GREEN(0xff, 8),
        BLUE (0xff, 0);

        private int mask;
        private int offset;

        Channel(int mask, int offset) {
            this.mask = mask;
            this.offset = offset;
        }

        public int readValue(int x, int y, NativeImage image) {
            return (image.getPixelRGBA(x, y) >> offset) & mask;
        }
    }
}