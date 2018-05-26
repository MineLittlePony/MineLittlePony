package com.minelittlepony.ducks;

import com.minelittlepony.model.ModelWrapper;

/**
 * I Render Pony now, oky?
 */
public interface IRenderPony {

    /**
     * Gets the wrapped pony model for this renderer.
     */
    ModelWrapper getPlayerModel();

    /**
     * Gets the current shadow size for rendering.
     */
    float getShadowScale();

    /**
     * Gets the scaling factor used when rendering this pony.
     */
    float getScaleFactor();
}
