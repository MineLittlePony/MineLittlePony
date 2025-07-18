package com.minelittlepony.api.model;

import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;

public interface PreviewRenderState {
    PlayerPonyRenderState getRenderState();

    void completeStateUpdate(PonyModel<?> model);
}
