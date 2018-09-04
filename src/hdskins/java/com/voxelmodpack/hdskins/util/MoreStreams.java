package com.voxelmodpack.hdskins.util;

import java.util.stream.Stream;
import javax.annotation.Nullable;

public class MoreStreams {

    public static <T> Stream<T> ofNullable(@Nullable T t) {
        return t == null ? Stream.empty() : Stream.of(t);
    }
}
