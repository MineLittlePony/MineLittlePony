package com.minelittlepony.api.events;

import net.fabricmc.fabric.api.event.Event;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
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
public interface PonyModelPrepareCallback extends PonyRenderStatePrepareCallback {
    @SuppressWarnings({"unchecked", "rawtypes"})
    Event<PonyModelPrepareCallback> EVENT = (Event)PonyRenderStatePrepareCallback.EVENT;

    @Override
    default void onPonyRenderStatePrepared(PonyRenderState state, PonyModel<?> model, ModelAttributes.Mode mode) {
        onPonyModelPrepared(state.getAttributes(), model, mode);
    }

    void onPonyModelPrepared(ModelAttributes attributes, PonyModel<?> model, ModelAttributes.Mode mode);
}
