package com.minelittlepony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.ModelAttributes;

public interface PonyModelPrepareCallback {

    Event<PonyModelPrepareCallback> EVENT = EventFactory.createArrayBacked(PonyModelPrepareCallback.class, listeners -> (entity, model, mode) -> {
        for (PonyModelPrepareCallback event : listeners) {
            event.onPonyModelPrepared(entity, model, mode);
        }
    });

    void onPonyModelPrepared(Entity entity, PonyModel<?> model, ModelAttributes.Mode mode);
}
