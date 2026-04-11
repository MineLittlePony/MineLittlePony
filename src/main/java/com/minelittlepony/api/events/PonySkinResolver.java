package com.minelittlepony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.Pony;

/**
 * Event for mods that want to replace the skin being used by a pony.
 */
public interface PonySkinResolver {
    Event<PonySkinResolver> EVENT = EventFactory.createArrayBacked(PonySkinResolver.class, listeners -> (entity, pony, result) -> {
        for (PonySkinResolver event : listeners) {
            result = event.onPonySkinResolving(entity, pony, result);
        }
        return result;
    });

    @Nullable
    Identifier onPonySkinResolving(Entity entity, PonyLookup ponyLookup, @Nullable Identifier previousResult);

    interface PonyLookup {
        Pony getPony(Identifier skin);
    }
}
