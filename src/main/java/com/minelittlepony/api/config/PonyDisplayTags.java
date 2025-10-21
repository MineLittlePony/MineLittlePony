package com.minelittlepony.api.config;

import net.minecraft.entity.Entity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.server.PonyCommandTagsStore;

import java.util.*;

/**
 * Command Tags you can apply to entities to force their pony mode either on or off.
 * <p>
 * Utilitizes custom fonts to allow storing values in the custom name field.
 *
 * /data merge entity ee7f6d46-3a1f-47a7-864a-95a5db10a539 {
 *      CustomName: [
 *          { font:"minelittlepony:metadata/size/foal", text: ""},
 *          { font: "default", text: "Bob"}
 *      ]
 * }
 */
public record PonyDisplayTags(boolean neverPonify, boolean alwaysPonify, Optional<Race> race, Optional<Size> size, OptionalInt magicColor) {
    public static final PonyDisplayTags EMPTY = new PonyDisplayTags(false, false, Optional.empty(), Optional.empty(), OptionalInt.empty());

    public static final String NEVER_PONIFY = "minelittlepony:never_ponify";
    public static final String ALWAYS_PONIFY = "minelittlepony:always_ponify";
    public static final String MAGIC_COLOR_OVERRIDE = "minelittlepony:metadata/magic_color";
    public static final String RACE_OVERRIDE = "minelittlepony:metadata/race";
    public static final String SIZE_OVERRIDE = "minelittlepony:metadata/size";

    public static PonyDisplayTags of(@Nullable Entity entity) {
        return PonyCommandTagsStore.INSTANCE.getTags(entity);
    }

    public boolean shouldPonify(boolean preference) {
        return !neverPonify() && (alwaysPonify() || preference);
    }
}
