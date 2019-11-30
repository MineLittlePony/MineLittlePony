package com.minelittlepony.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BasicEasingInterpolator implements IInterpolator {

    private static LoadingCache<UUID, BasicEasingInterpolator> instanceCache = CacheBuilder.newBuilder()
        .expireAfterAccess(30, TimeUnit.SECONDS)
        .build(CacheLoader.from(BasicEasingInterpolator::new));

    /**
     * Gets or creates a new basic, linear interpolator for the provided id.
     */
    public static IInterpolator getInstance(UUID id) {
        return instanceCache.getUnchecked(id);
    }

    private final Map<String, Float> properties = new HashMap<>();

    private float getLast(String key, float to) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }

        return to;
    }

    @Override
    public float interpolate(String key, float to, float scalingFactor) {
        float from = getLast(key, to);

        from += (to - from) / scalingFactor;

        if (Float.isNaN(from) || Float.isInfinite(from)) {
            System.err.println("Error: Animation frame for " + key + " is NaN or Infinite.");
            from = to;
        }

        properties.put(key, from);

        return from;

    }

}
