package com.minelittlepony.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;

public class MoreStreams {

    public static <T> Stream<T> ofNullable(@Nullable T t) {
        return t == null ? Stream.empty() : Stream.of(t);
    }

    public static <T, V> V[] map(T[] items, Function<T, V> converter, IntFunction<V[]> collector) {
        return Lists.newArrayList(items)
                .stream()
                .map(converter)
                .toArray(collector);
    }

    public static <T, V> List<V> map(List<T> items, Function<T, V> converter) {
        return items.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    public static <T> List<T> distinct(List<T> input) {
        return input.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
