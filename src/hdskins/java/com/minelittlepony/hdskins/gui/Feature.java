package com.minelittlepony.hdskins.gui;

/**
 * Represents the possible features that a skin server can implement.
 */
public enum Feature {
    /**
     * Whether a server has write access.
     * i.e. If the server allows for users to upload a new skin.
     */
    UPLOAD_USER_SKIN,
    /**
     * Whether a server allows for downloading and saving a user's skin.
     * Most servers should support this.
     */
    DOWNLOAD_USER_SKIN,
    /**
     * Whether a server has delete access.
     * i.e. If the server allows a user to deleted a previously uploaded skin.
     */
    DELETE_USER_SKIN,
    /**
     * Whether a server can send a full list of skins for a given profile.
     * Typically used for servers that keep a record of past uploads
     * and/or allow for switching between past skins.
     */
    FETCH_SKIN_LIST,
    /**
     * Whether a server supports thin (Alex) skins or just default (Steve) skins.
     * Servers without this will typically fall back to using the player's uuid on the client side.
     *
     * (unused)
     */
    MODEL_VARIANTS,
    /**
     * Whether a server allows for uploading alternative skin types. i.e. Cape, Elytra, Hats and wears.
     */
    MODEL_TYPES,
    /**
     * Whether a server will accept arbitrary extra metadata values with skin uploads.
     *
     * (unused)
     */
    MODEL_METADATA,
    /**
     * Whether a server can provide a link to view a user's profile online,
     * typically through a web-portal.
     */
    LINK_PROFILE
}
