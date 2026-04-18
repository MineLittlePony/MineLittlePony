package com.minelittlepony.api.state;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.pony.meta.Race;

/**
 * Render state belonging to a dummy model player.
 */
public interface PreviewRenderState {
    void completeStateUpdate(Models<?> models);

    Race getRace();
}
