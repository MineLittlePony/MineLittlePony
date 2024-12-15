package com.minelittlepony.api.pony.meta;

import net.minecraft.network.PacketByteBuf;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;
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

    public static <T extends Enum<T> & TValue<T>> Flags<T> of(T def) {
        return new Flags<>(def, Set.<T>of(), 0);
    }

    public static <T extends Enum<T> & TValue<T>> Flags<T> of(T def, int colorCode, Set<T> values) {
        return new Flags<>(def, values, colorCode);
    }

    public static <T extends Enum<T> & TValue<T>> Flags<T> read(T def, PacketByteBuf buffer) {
        int length = buffer.readVarInt();
        @SuppressWarnings("unchecked")
        Set<T> values = EnumSet.noneOf((Class<T>)def.getClass());
        @SuppressWarnings("unchecked")
        T[] all = (T[])def.getClass().getEnumConstants();
        for (int i = 0; i < length; i++) {
            values.add(all[buffer.readInt()]);
        }
        return of(def, buffer.readInt(), values);
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeCollection(values, (buf, value) -> buf.writeInt(value.ordinal()));
        buffer.writeInt(colorCode);
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
