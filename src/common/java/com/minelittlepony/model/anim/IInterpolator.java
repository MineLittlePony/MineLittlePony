package com.minelittlepony.model.anim;

/**
 * Interpolator function for handling transitions between animation states.
 */
@FunctionalInterface
public interface IInterpolator {
    /**
     * Interpolates a value between the requested final destination and what it was last.
     *
     * @param key           Identifier to track previous values
     * @param to            The new values
     * @param scalingFactor Scaling factor to control how quickly values change
     */
    float interpolate(String key, float to, float scalingFactor);
}
