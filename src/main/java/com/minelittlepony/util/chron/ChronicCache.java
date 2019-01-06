package com.minelittlepony.util.chron;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Special version of a map that culls its own values.
 */
public class ChronicCache<K, V extends Touchable<V>> extends HashMap<K, V> {
    private static final long serialVersionUID = 6454924015818181978L;

    public V retrieve(K key, Function<? super K, ? extends V> mappingFunction) {
        V result = computeIfAbsent(key, mappingFunction).touch();

        entrySet().removeIf(entry -> entry.getValue().hasExpired());

        return result;
    }

}
