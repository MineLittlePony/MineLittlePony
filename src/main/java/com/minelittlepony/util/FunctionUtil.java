package com.minelittlepony.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface FunctionUtil {
    static <A, B> Function<A, B> memoize(Function<A, B> func, Function<A, String> keyFunc) {
        final Map<String, B> cache = new ConcurrentHashMap<>();
        return a -> cache.computeIfAbsent(keyFunc.apply(a), k -> func.apply(a));
    }
}
