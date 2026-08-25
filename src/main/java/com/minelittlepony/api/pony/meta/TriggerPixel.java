package com.minelittlepony.api.pony.meta;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ARGB;

import org.joml.Vector2i;

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
    TriggerPixel<HornLength> HORN_LENGTH = ofOptions(3, 1, HornLength.FULL, HornLength.values());
    TriggerPixel<Integer> CHANGELING_ANTLERS = ofColor(3, 2);

    static <T extends TValue<T>> TriggerPixel<T> ofOptions(int x, int y, T def, T[] options) {
        MAX_COORDS.x = Math.max(MAX_COORDS.x, x);
        MAX_COORDS.y = Math.max(MAX_COORDS.y, y);
        Int2ObjectOpenHashMap<T> lookup = buildLookup(options);
        return image -> {
            int color = image.getColor(x, y);

            if (ARGB.alpha(color) < 255) {
                return (T)def;
            }
            return lookup.getOrDefault(color & 0x00FFFFFF, def);
        };
    }

    static TriggerPixel<Integer> ofColor(int x, int y) {
        MAX_COORDS.x = Math.max(MAX_COORDS.x, x);
        MAX_COORDS.y = Math.max(MAX_COORDS.y, y);
        return image -> image.getColor(x, y);
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
            int color = image.getColor(x, y);
            if (ARGB.alpha(color) < 255) {
                return def;
            }
            @SuppressWarnings("unchecked")
            Set<T> values = EnumSet.noneOf((Class<T>)def.def().getClass());
            if (flagReader.readFlag(ARGB.red(color), values)
                    | flagReader.readFlag(ARGB.green(color), values)
                    | flagReader.readFlag(ARGB.blue(color), values)) {
                return new Flags<>(def.def(), values, color & 0x00FFFFFF);
            }
            return def;
        };
    }

    static <T extends TValue<T>> Int2ObjectOpenHashMap<T> buildLookup(T[] options) {
        Int2ObjectOpenHashMap<T> lookup = new Int2ObjectOpenHashMap<>();
        for (T option : options) {
            lookup.put(option.colorCode(), option);
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
