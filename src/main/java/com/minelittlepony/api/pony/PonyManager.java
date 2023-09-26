package com.minelittlepony.api.pony;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with the skin of an entity.
 */
public interface PonyManager  {
    /**
     * Gets a pony representation of the passed in entity.
     *
     * If the supplied entity is null or can't be determined to be a pony, returns the empty optional.
     */
    default Optional<Pony> getPony(@Nullable Entity entity) {
        return entity instanceof LivingEntity living ? getPony(living) : Optional.empty();
    }

    /**
     * Gets a pony representation of the passed in entity.
     *
     * If the supplied entity is null or can't be determined to be a pony, returns the empty optional.
     */
    Optional<Pony> getPony(LivingEntity entity);

    /**
     * Gets a random background pony determined by the given uuid.
     *
     * Useful for mods that offer customisation, especially ones that have a whole lot of NPCs.
     *
     * @param uuid id of a player
     */
    Pony getBackgroundPony(UUID uuid);

    /**
     * Gets or creates a pony for the given player.
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param player the player
     */
    Pony getPony(PlayerEntity player);

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
    Pony getPony(@Nullable Identifier resource, @Nullable UUID uuid);

    /**
     * Gets or creates a pony for the given skin resource and vanilla model type.
     *
     * @param resource A texture resource
     */
    default Pony getPony(Identifier resource) {
        return getPony(resource, null);
    }

    interface ForcedPony {}

    final class Instance {
        public static PonyManager instance;
    }
}
