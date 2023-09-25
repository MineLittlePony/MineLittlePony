package com.minelittlepony.api.pony.meta;

import net.minecraft.network.PacketByteBuf;

import java.util.*;

public record Flags<T extends Enum<T>> (
        boolean[] flags,
        List<T> values,
        int colorCode
    ) implements Comparable<Flags<T>> {

    public static <T extends Enum<T>> Flags<T> of(Class<T> type) {
        return new Flags<>(new boolean[type.getEnumConstants().length], List.<T>of(), 0);
    }

    public static <T extends Enum<T>> Flags<T> of(Class<T> type, int colorCode, boolean...flags) {
        return new Flags<>(flags, flags(type.getEnumConstants(), flags), colorCode);
    }

    public static <T extends Enum<T>> Flags<T> read(Class<T> type, PacketByteBuf buffer) {
        int length = buffer.readVarInt();
        List<T> values = new ArrayList<>();
        T[] all = type.getEnumConstants();
        boolean[] flags = new boolean[all.length];
        for (int i = 0; i < length; i++) {
            values.add(all[buffer.readInt()]);
            flags[i] = true;
        }
        return new Flags<>(flags, values, buffer.readInt());
    }

    public static <T> List<T> flags(T[] values, boolean[] flags) {
        List<T> wears = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (flags[i]) wears.add(values[i]);
        }
        return wears;
    }

    public boolean includes(T t) {
        return flags[t.ordinal()];
    }

    public int compareTo(Flags<T> other) {
        return Arrays.compare(flags, other.flags);
    }

    public void write(PacketByteBuf buffer) {
        buffer.writeCollection(values, (buf, value) -> buf.writeInt(value.ordinal()));
        buffer.writeInt(colorCode);
    }
}
