package com.minelittlepony.api.model;

import com.minelittlepony.client.render.entity.state.PonyRenderState;

public interface HornedPonyModel<T extends PonyRenderState> extends PonyModel<T> {
    /**
     * Returns true if this model is being applied to a race that can use magic.
     */
    default boolean hasMagic(T state) {
        return state.getRace().hasHorn() && state.attributes.metadata.glowColor() != 0;
    }

    /**
     * Returns true if this model is currently using magic (horn is lit).
     */
    boolean isCasting(T state);
}
