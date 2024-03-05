package com.minelittlepony.api.pony.meta;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.ColorHelper;

import org.joml.Vector2i;

import com.minelittlepony.common.util.Color;

import java.util.*;

/**
 * Individual trigger pixels for a pony skin.
 */
public interface TriggerPixel<T> {
    Vector2i MAX_COORDS = new Vector2i();

    TriggerPixel<Race> RACE = ofOptions(0, 0, Race.HUMAN, Race.values());
    TriggerPixel<TailLength> TAIL = ofOptions(1, 0, TailLength.FULL, TailLength.values());
    TriggerPixel<Gender> GENDER = ofOptions(2, 0, Gender.MARE, Gender.values());
    TriggerPixel<TailShape> TAIL_SHAPE = ofOptions(2, 1, TailShape.STRAIGHT, TailShape.values());
    TriggerPixel<Size> SIZE = ofOptions(3, 0, SizePreset.NORMAL, SizePreset.values());
    TriggerPixel<Integer> GLOW = ofColor(0, 1);
    TriggerPixel<Flags<Wearable>> WEARABLES = ofFlags(1, 1, Wearable.EMPTY_FLAGS, Wearable.values());
    TriggerPixel<Integer> PRIORITY = ofColor(2, 2);

    static <T extends TValue<T>> TriggerPixel<T> ofOptions(int x, int y, T def, T[] options) {
        MAX_COORDS.x = Math.max(MAX_COORDS.x, x);
        MAX_COORDS.y = Math.max(MAX_COORDS.y, y);
        Int2ObjectOpenHashMap<T> lookup = buildLookup(options);
        return image -> {
            int color = Color.abgrToArgb(image.getColor(x, y));

            if (ColorHelper.Argb.getAlpha(color) < 255) {
                return (T)def;
            }
            return lookup.getOrDefault(color & 0x00FFFFFF, def);
        };
    }

    static TriggerPixel<Integer> ofColor(int x, int y) {
        MAX_COORDS.x = Math.max(MAX_COORDS.x, x);
        MAX_COORDS.y = Math.max(MAX_COORDS.y, y);
        return image -> Color.abgrToArgb(image.getColor(x, y));
    }

    static <T extends Enum<T> & TValue<T>> TriggerPixel<Flags<T>> ofFlags(int x, int y, Flags<T> def, T[] options) {
        MAX_COORDS.x = Math.max(MAX_COORDS.x, x);
        MAX_COORDS.y = Math.max(MAX_COORDS.y, y);
        Int2ObjectOpenHashMap<T> lookup = buildLookup(options);
        var flagReader = new Object() {
            boolean readFlag(int color, Set<T> values) {
                T value = lookup.get(color);
                return value != null && values.add(value);
            }
        };
        return image -> {
            int color = Color.abgrToArgb(image.getColor(x, y));
            if (ColorHelper.Argb.getAlpha(color) < 255) {
                return def;
            }
            @SuppressWarnings("unchecked")
            Set<T> values = EnumSet.noneOf((Class<T>)def.def().getClass());
            if (flagReader.readFlag(ColorHelper.Argb.getRed(color), values)
                    | flagReader.readFlag(ColorHelper.Argb.getGreen(color), values)
                    | flagReader.readFlag(ColorHelper.Argb.getBlue(color), values)) {
                return new Flags<>(def.def(), values, color & 0x00FFFFFF);
            }
            return def;
        };
    }

    static <T extends TValue<T>> Int2ObjectOpenHashMap<T> buildLookup(T[] options) {
        Int2ObjectOpenHashMap<T> lookup = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < options.length; i++) {
            lookup.put(options[i].colorCode(), options[i]);
        }
        return lookup;
    }


    T read(Mat image);

    static boolean isTriggerPixelCoord(int x, int y) {
        return x <= MAX_COORDS.x && y <= MAX_COORDS.y;
    }

    interface Mat {
        int getColor(int x, int y);
    }

}
