package com.minelittlepony.util.animation;

import com.minelittlepony.util.chron.ChronicCache;
import com.minelittlepony.util.chron.Touchable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasicEasingInterpolator extends Touchable<BasicEasingInterpolator> implements IInterpolator {

    private static ChronicCache<UUID, BasicEasingInterpolator> instanceCache = new ChronicCache<>();

    /**
     * Gets or creates a new basic, linear interpolator for the provided id.
     */
    public static IInterpolator getInstance(UUID id) {
        return instanceCache.retrieve(id, BasicEasingInterpolator::new);
    }

    public BasicEasingInterpolator() {

    }

    private BasicEasingInterpolator(UUID id) {

    }

    private final Map<String, Float> properties = new HashMap<String, Float>();

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
