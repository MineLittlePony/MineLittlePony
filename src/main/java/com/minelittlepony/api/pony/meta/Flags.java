package com.minelittlepony.api.pony.meta;

import net.minecraft.network.PacketByteBuf;

import java.util.*;

public record Flags<T extends Enum<T> & TValue<T>> (
        T def,
        Set<T> values,
        int colorCode
    ) implements Comparable<Flags<T>>, TValue<T> {

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
        return new Flags<>(def, values, buffer.readInt());
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
