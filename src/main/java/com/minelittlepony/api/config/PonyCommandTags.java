package com.minelittlepony.api.config;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.mixin.NbtAccessor;

/**
 * Command Tags you can apply to entities to force their pony mode either on or off.
 */
public interface PonyCommandTags {
    String NEVER_PONIFY = "minelittlepony:never_ponify";
    String ALWAYS_PONIFY = "minelittlepony:always_ponify";
    String MAGIC_COLOR_OVERRIDE = "minelittlepony:magic_color_override";

    static boolean isAlwaysAPony(@Nullable Entity entity) {
        return entity != null && entity.getCommandTags().contains(ALWAYS_PONIFY);
    }

    static boolean isNeverAPony(@Nullable Entity entity) {
        return entity != null && entity.getCommandTags().contains(NEVER_PONIFY);
    }

    static boolean isAPony(@Nullable Entity entity, boolean preference) {
        return !isNeverAPony(entity) && (isAlwaysAPony(entity) || preference);
    }

    static int getMagicColorOverride(@Nullable Entity entity, int color) {
        if (entity == null) {
            return color;
        }
        NbtComponent customData = entity.get(DataComponentTypes.CUSTOM_DATA);
        return customData == null ? color : ((NbtAccessor)(Object)customData).minelp_getNbt().getInt(MAGIC_COLOR_OVERRIDE, color);
    }
}
