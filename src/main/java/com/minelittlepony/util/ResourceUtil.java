package com.minelittlepony.util;

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
}
