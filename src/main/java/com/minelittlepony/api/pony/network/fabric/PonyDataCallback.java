package com.minelittlepony.api.pony.network.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.api.pony.PonyData;

/**
 * Callback triggered on the server when receiving pony data from a client.
 *
 */
public interface PonyDataCallback {
    Event<PonyDataCallback> EVENT = EventFactory.createArrayBacked(PonyDataCallback.class, listeners -> (sender, data, env) -> {
        for (PonyDataCallback event : listeners) {
            event.onPonyDataAvailable(sender, data, env);
        }
    });

    /**
     * Called when pony data is received.
     * @param sender The player who sent the data - this is the owner of the skin/pony data.
     * @param data   The skin/pony data
     * @param env    The environment. Whether this call is coming from the client or server. Clients may get two calls, one for both.
     */
    void onPonyDataAvailable(PlayerEntity sender, PonyData data, EnvType env);
}
