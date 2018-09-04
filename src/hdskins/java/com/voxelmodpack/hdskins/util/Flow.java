package com.voxelmodpack.hdskins.util;

import com.google.common.base.Optional;
import com.google.common.collect.Streams;

import java.util.stream.Stream;

public interface Flow<T> {

    static <T> Stream<T> from(T obj) {
        return Streams.stream(Optional.fromNullable(obj));
    }
}
