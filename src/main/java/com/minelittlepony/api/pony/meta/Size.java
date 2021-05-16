package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

/**
 * Represents the different model sizes that are possible.
 *
 * For a list of possible presets, look at {@link Sizes}.
 * This interface exists for servers so they can work with this information even though they might not have access to the client config.
 *
 */
public interface Size extends TriggerPixelType<Size> {
    /**
     * The Enum index of this size. May be used on the client to convert to an instance of Sizes or use {@link Sizes#of}
     *
     * Made to be compatible with the enum variant.
     */
    int ordinal();

    /**
     * Name of the size.
     *
     * Made to be compatible with the enum variant.
     */
    String name();

    /**
     * A scale factor that controls the size of the shadow that appears under the entity.
     */
    float getShadowSize();

    /**
     * The global scale factor applied to all physical dimensions.
     */
    float getScaleFactor();

    /**
     * A scale factor used to alter the vertical eye position.
     */
    float getEyeHeightFactor();

    /**
     * A scale factor used to alter the camera's distance.
     */
    float getEyeDistanceFactor();
}
