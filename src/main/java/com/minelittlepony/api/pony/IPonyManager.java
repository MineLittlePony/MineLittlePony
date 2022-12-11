package com.minelittlepony.api.pony;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public interface IPonyManager  {
    /**
     * Gets a pony representation of the passed in entity.
     *
     * If the supplied entity is null or can't be determined to be a pony, returns the empty optional.
     */
    Optional<IPony> getPony(@Nullable Entity entity);

    /**
     * Gets or creates a pony for the given player.
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param player the player
     */
    IPony getPony(PlayerEntity player);

    /**
     * Gets or creates a pony for the given skin resource and vanilla model type.
     *
     * @param resource A texture resource
     */
    IPony getPony(Identifier resource);

    /**
     * Gets or creates a pony for the given skin resource and entity id.
     *
     * Whether is has slim arms is determined by the id.
     *
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param resource A texture resource
     * @param uuid id of a player
     */
    IPony getPony(Identifier resource, UUID uuid);

    /**
     * Gets a random background pony determined by the given uuid.
     *
     * Useful for mods that offer customisation, especially ones that have a whole lot of NPCs.
     *
     * @param uuid  A UUID. Either a user or an entity.
     */
    IPony getBackgroundPony(UUID uuid);

    /**
     * De-registers a pony from the cache.
     */
    void removePony(Identifier resource);

    interface ForcedPony {}

    final class Instance {
        public static IPonyManager instance;
    }
}
