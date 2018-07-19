package com.voxelmodpack.hdskins.util;

import java.util.Optional;

/**
 * Silly optionals
 */
public final class Optionals {
    public static <T> T nullableOf(Optional<T> optional) {
        return getOrDefault(optional, null);
    }

    public static <T> T getOrDefault(Optional<T> optional, T def) {
        return optional.isPresent() ? optional.get() : def;
    }
}
