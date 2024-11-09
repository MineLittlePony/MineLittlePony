package com.minelittlepony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.ModelAttributes;

/**
 * Event triggered when a pony model's state is being evaluated.
 * <p>
 * Subscribers have the option to read the pony model's attributes or modify them if neccessary to
 * allow for custom animations.
 */
public interface PonyModelPrepareCallback {
    Event<PonyModelPrepareCallback> EVENT = EventFactory.createArrayBacked(PonyModelPrepareCallback.class, listeners -> (entity, model, mode) -> {
        for (PonyModelPrepareCallback event : listeners) {
            event.onPonyModelPrepared(entity, model, mode);
        }
    });

    void onPonyModelPrepared(ModelAttributes attributes, PonyModel<?> model, ModelAttributes.Mode mode);
}
