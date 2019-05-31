package com.minelittlepony.util.resources;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Simple mapping from an integer index to a pre-defined set of strings.
 * Returns the string representation of the index if no value was found.
 */
public class IntStringMapper implements Function<Integer, String> {

    private final HashMap<Integer, String> values = Maps.newHashMap();

    /**
     * Creates a new string mapper pre-populated with the provided values by index.
     */
    public IntStringMapper(String...values) {
        for (int i = 0; i < values.length; i++) {
            this.values.put(i, Objects.requireNonNull(values[i]));
        }
    }

    /**
     * Return the values. So you can actually modify it.
     * I mean, if you want to @modders...
     */
    public Map<Integer, String> getValues() {
        return values;
    }

    @Override
    public String apply(Integer t) {
        if (values.containsKey(t)) {
            return values.get(t);
        }
        return String.valueOf(t);
    }
}
