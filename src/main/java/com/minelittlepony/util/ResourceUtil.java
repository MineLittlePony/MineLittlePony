package com.minelittlepony.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public final class ResourceUtil {
    public static String format(String template, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof Number)) {
                args[i] = toPathComponent(args[i]);
            }
        }
        return String.format(template, args);
    }

    private static String toPathComponent(Object value) {
        return value.toString().toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
    }

    public static boolean textureExists(Identifier texture) {
        return Minecraft.getInstance().getResourceManager().getResource(texture).isPresent();
    }

    public static Optional<Identifier> verifyTexture(Identifier texture) {
        return Minecraft.getInstance().getResourceManager().getResource(texture).map(_ -> texture);
    }
}
