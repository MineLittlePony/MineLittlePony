package com.minelittlepony.api.pony;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * The PonyManager is responsible for reading and recoding all the pony data associated with an entity of skin.
 *
 */
public interface IPonyManager  {

    public static final Identifier STEVE = new Identifier("minelittlepony", "textures/entity/steve_pony.png");
    public static final Identifier ALEX = new Identifier("minelittlepony", "textures/entity/alex_pony.png");

    public static final String BGPONIES_JSON = "textures/entity/pony/bgponies.json";

    /**
     * Gets or creates a pony for the given player.
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param player the player
     */
    public IPony getPony(PlayerEntity player);

    /**
     * Gets or creates a pony for the given skin resource and vanilla model type.
     *
     * @param resource A texture resource
     */
    public IPony getPony(Identifier resource);

    /**
     * Gets or creates a pony for the given skin resource and entity id.
     *
     * Whether is has slim arms is determined by the id.
     *
     * Delegates to the background-ponies registry if no pony skins were available and client settings allows it.
     *
     * @param resource A texture resource
     * @param uuid id of a player or entity
     */
    IPony getPony(Identifier resource, UUID uuid);

    /**
     * Gets the default pony. Either STEVE/ALEX, or a background pony based on client settings.
     *
     * @param uuid id of a player or entity
     */
    IPony getDefaultPony(UUID uuid);

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

    public static Identifier getDefaultSkin(UUID uuid) {
        return isSlimSkin(uuid) ? ALEX : STEVE;
    }

    /**
     * Returns true if the given uuid is of a player would would use the ALEX skin type.
     */
    public static boolean isSlimSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }
}
