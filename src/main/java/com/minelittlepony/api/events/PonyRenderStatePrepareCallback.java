package com.minelittlepony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.api.model.ModelAttributes;

/**
 * Event triggered when a pony model's state is being evaluated.
 * <p>
 * Subscribers have the option to read the pony model's attributes or modify them if neccessary to
 * allow for custom animations.
 */
public interface PonyRenderStatePrepareCallback {
    Event<PonyRenderStatePrepareCallback> EVENT = EventFactory.createArrayBacked(PonyRenderStatePrepareCallback.class, listeners -> (state, model, mode) -> {
        for (PonyRenderStatePrepareCallback event : listeners) {
            event.onPonyRenderStatePrepared(state, model, mode);
        }
    });

    void onPonyRenderStatePrepared(PonyRenderState state, PonyModel<?> model, ModelAttributes.Mode mode);
}
