package com.minelittlepony.api.pony.meta;

import net.minecraft.client.texture.NativeImage;

import com.minelittlepony.api.pony.TriggerPixelSet;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.TriggerPixelValue;
import com.minelittlepony.common.util.Color;

import java.util.Arrays;

/**
 * Individual trigger pixels for a pony skin.
 *
 */
@SuppressWarnings("unchecked")
public enum TriggerPixel {
    RACE(Race.HUMAN, Channel.ALL, 0, 0),
    TAIL(TailLength.FULL, Channel.ALL, 1, 0),
    GENDER(Gender.MARE, Channel.ALL, 2, 0),
    SIZE(Sizes.NORMAL, Channel.ALL, 3, 0),
    GLOW(null, Channel.RAW, 0, 1),
    WEARABLES(Wearable.NONE, Channel.RAW, 1, 1),
    TAIL_SHAPE(TailShape.STRAIGHT, Channel.ALL, 2, 1);

    private int x;
    private int y;

    private Channel channel;

    TriggerPixelType<?> def;

    private static final TriggerPixel[] VALUES = values();
    private static final int MAX_READ_X = Arrays.stream(VALUES).mapToInt(i -> i.x).max().getAsInt();
    private static final int MAX_READ_Y = Arrays.stream(VALUES).mapToInt(i -> i.y).max().getAsInt();

    TriggerPixel(TriggerPixelType<?> def, Channel channel, int x, int y) {
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
    public <T extends TriggerPixelType<T>> TriggerPixelValue<T> readValue(NativeImage image) {
        int color = readColor(image);

        if (Channel.ALPHA.readValue(x, y, image) < 255) {
            return new TriggerPixelValue<>(color, (T)def);
        }

        return new TriggerPixelValue<>(color, TriggerPixelType.getByTriggerPixel((T)def, color));
    }

    public <T extends Enum<T> & TriggerPixelType<T>> TriggerPixelSet<T> readFlags(NativeImage image) {
        boolean[] out = new boolean[def.getClass().getEnumConstants().length];
        readFlags(out, image);
        return new TriggerPixelSet<>(readColor(image), (T)def, out);
    }

    public <T extends Enum<T> & TriggerPixelType<T>> void readFlags(boolean[] out, NativeImage image) {
        readFlag(out, Channel.RED, image);
        readFlag(out, Channel.GREEN, image);
        readFlag(out, Channel.BLUE, image);
    }

    private <T extends Enum<T> & TriggerPixelType<T>> void readFlag(boolean[] out, Channel channel, NativeImage image) {

        if (Channel.ALPHA.readValue(x, y, image) < 255) {
            return;
        }

        T value = TriggerPixelType.getByTriggerPixel((T)def, channel.readValue(x, y, image));

        out[value.ordinal()] |= value != def;
    }

    public static boolean isTriggerPixelCoord(int x, int y) {
        return x <= MAX_READ_X && y <= MAX_READ_Y;
    }

    enum Channel {
        RAW  (0xFFFFFFFF, 0),
        ALL  (0x00FFFFFF, 0),
        ALPHA(0x000000FF, 24),
        RED  (0x000000FF, 0),
        GREEN(0x000000FF, 8),
        BLUE (0x000000FF, 16);

        private int mask;
        private int offset;

        Channel(int mask, int offset) {
            this.mask = mask;
            this.offset = offset;
        }

        public int readValue(int x, int y, NativeImage image) {
            return (Color.abgrToArgb(image.getColor(x, y)) >> offset) & mask;
        }
    }
}
