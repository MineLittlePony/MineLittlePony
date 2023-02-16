package com.minelittlepony.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

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
        return
            MinecraftClient.getInstance().getTextureManager().getOrDefault(texture, null) != null
            || MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent();
    }

    public static Optional<Identifier> verifyTexture(Identifier texture) {
        return textureExists(texture) ? Optional.of(texture) : Optional.empty();
    }
}
