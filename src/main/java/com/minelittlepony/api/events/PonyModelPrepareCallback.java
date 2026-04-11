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
 *
 * @deprecated Replace with {@link PonyRenderStatePrepareCallback}
 */
@Deprecated(forRemoval = true)
public interface PonyModelPrepareCallback {
    @Deprecated(forRemoval = true)
    Event<PonyModelPrepareCallback> EVENT = EventFactory.createArrayBacked(PonyModelPrepareCallback.class, listeners -> (attributes, model, mode) -> {
        for (PonyModelPrepareCallback event : listeners) {
            event.onPonyModelPrepared(attributes, model, mode);
        }
    });

    @Deprecated(forRemoval = true)
    void onPonyModelPrepared(ModelAttributes attributes, PonyModel<?> model, ModelAttributes.Mode mode);
}
