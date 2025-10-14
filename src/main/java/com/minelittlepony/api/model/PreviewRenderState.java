package com.minelittlepony.api.model;

import com.minelittlepony.api.pony.meta.Race;

public interface PreviewRenderState {
    void completeStateUpdate(Models<?> models);

    Race getRace();
}
