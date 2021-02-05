package com.minelittlepony.model.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.model.IModel;

public interface PonyModelPrepareCallback {

    Event<PonyModelPrepareCallback> EVENT = EventFactory.createArrayBacked(PonyModelPrepareCallback.class, listeners -> (entity, model, mode) -> {
        for (PonyModelPrepareCallback event : listeners) {
            event.onPonyModelPrepared(entity, model, mode);
        }
    });

    void onPonyModelPrepared(Entity entity, IModel model, EquineRenderManager.Mode mode);
}
