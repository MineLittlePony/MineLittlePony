package com.minelittlepony.api.pony.meta;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

import java.util.*;
import java.util.function.Supplier;

/**
 * Interface for enums that can be parsed from an image trigger pixel value.
 */
public interface TValue<T> extends StringRepresentable {
    /**
     * Gets the pixel colour matching this enum value.
     */
    int colorCode();

    /**
     * Gets the pixel colour matching this enum value, adjusted to fill all three channels.
     */
    default int getChannelAdjustedColorCode() {
        return colorCode();
    }

    /**
     * Gets a string representation of this value.
     */
    String name();

    @Override
    default String getSerializedName() {
        return name();
    }

    default String getHexValue() {
        return toHex(colorCode());
    }

    /**
     * Returns a list of possible values this trigger pixel can accept.
     */
    @SuppressWarnings("unchecked")
    default List<TValue<T>> getOptions() {
        if (this instanceof Enum) {
            // cast is required because gradle's compiler is more strict
            return Arrays.asList(getClass().getEnumConstants());
        }
        return List.of();
    }

    default boolean matches(TValue<?> o) {
        return o != null && colorCode() == o.colorCode();
    }

    static String toHex(int color) {
        String v = Integer.toHexString(color).toUpperCase();
        while (v.length() < 6) {
            v = "0" + v;
        }
        return "#" + v;
    }

    public record Numeric(int colorCode) implements TValue<Integer> {
        @Override
        public String name() {
            return "[Numeric " + getHexValue() + "]";
        }

        @Override
        public List<TValue<Integer>> getOptions() {
            return List.of();
        }
    }

    static <T extends Enum<T> & TValue<? super T>> Codecs<T, EnumCodec<T>> codecs(Supplier<T[]> valueGetter) {
        final T[] values = valueGetter.get();
        return new Codecs<>(
                StringRepresentable.fromEnum(() -> values),
                ByteBufCodecs.idMapper(id -> values[id], Enum::ordinal)
        );
    }

    static <T extends Enum<T> & TValue<? super T>> EnumCodec<T> enumCodec(Supplier<T[]> valueGetter) {
        return StringRepresentable.fromEnum(valueGetter);
    }

    public record Codecs<T extends TValue<? super T>, C extends Codec<T>>(
            C codec,
            StreamCodec<ByteBuf, T> streamCodec
    ) {}
}
