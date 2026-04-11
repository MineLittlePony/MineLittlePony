package com.minelittlepony.api.pony.meta;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record Flags<T extends Enum<T> & TValue<T>> (
        T def,
        Set<T> values,
        int colorCode
    ) implements Comparable<Flags<T>>, TValue<T> {

    public static <T extends Enum<T> & TValue<T>> Codec<Flags<T>> codec(T def, Codec<T> elementCodec) {
        Codec<Set<T>> setCodec = Codec.list(elementCodec).xmap(elements -> elements.stream().distinct().collect(Collectors.toUnmodifiableSet()), set -> List.copyOf(set));
        return Codec.xor(setCodec.xmap(elements -> new Flags<>(def, elements, 0), flags -> flags.values()), RecordCodecBuilder.<Flags<T>>create(i -> i.group(
                elementCodec.fieldOf("def").forGetter(Flags::def),
                setCodec.fieldOf("values").forGetter(Flags::values),
                Codec.INT.fieldOf("colorCode").forGetter(Flags::colorCode)
        ).apply(i, Flags::new))).xmap(Either::unwrap, Either::left);
    }

    public static <B extends ByteBuf, T extends Enum<T> & TValue<T>> StreamCodec<B, Flags<T>> streamCodec(T def, Supplier<T[]> valuesGetter) {
        return StreamCodec.composite(
                valueSetCodec(valuesGetter), Flags::values,
                ByteBufCodecs.INT, Flags::colorCode,
                (values, colorCode) -> of(def, colorCode, values)
        );
    }

    private static <T extends Enum<T> & TValue<T>> StreamCodec<ByteBuf, Set<T>> valueSetCodec(Supplier<T[]> valuesGetter) {
        final T[] values = valuesGetter.get();
        return ByteBufCodecs.INT.apply(ByteBufCodecs.list()).map(indexesList -> {
            @SuppressWarnings("unchecked")
            Set<T> set = EnumSet.noneOf(values[0].getClass());
            indexesList.forEach(ordinal -> set.add(values[ordinal]));
            return set;
        }, set -> set.stream().map(Enum::ordinal).toList());
    }

    public static <T extends Enum<T> & TValue<T>> Flags<T> of(T def) {
        return of(def, 0, Set.of());
    }

    public static <T extends Enum<T> & TValue<T>> Flags<T> of(T def, int colorCode, Set<T> values) {
        return new Flags<>(def, values, colorCode);
    }

    @Override
    public String name() {
        return "[Flags " + values + "]";
    }

    @Override
    public List<TValue<T>> getOptions() {
        return def.getOptions();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(TValue<?> o) {
        return o.getClass() == def.getClass() && values.contains((T)o);
    }

    @Override
    public int compareTo(Flags<T> other) {
        return Integer.compare(colorCode(), other.colorCode());
    }

}
