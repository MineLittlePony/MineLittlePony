package com.minelittlepony.api.pony.network.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.MineLittlePony;

/**
 * Callback triggered on the server when receiving pony data from a client.
 *
 */
public interface PonyDataCallback {

    Event<PonyDataCallback> EVENT = EventFactory.createArrayBacked(PonyDataCallback.class, listeners -> (sender, data, noSkin, env) -> {
        MineLittlePony.logger.info("Got pony data on the " + env + " from " + sender.getName().getString() + " with " + (noSkin ? "un" : "") + "set skin and he is a " + data.getRace() + "!");
        for (PonyDataCallback event : listeners) {
            event.onPonyDataAvailable(sender, data, noSkin, env);
        }
    });

    /**
     * Called when pony data is received.
     * @param sender The player who sent the data - this is the owner of the skin/pony data.
     * @param data   The skin/pony data
     * @param noSkin Whether the data is for a player with a default/unset custom skin.
     * @param env    The environment. Whether this call is coming from the client or server. Clients may get two calls, one for both.
     */
    void onPonyDataAvailable(PlayerEntity sender, IPonyData data, boolean noSkin, EnvType env);
}
