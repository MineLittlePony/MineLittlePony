package com.minelittlepony.client.compat.iris;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;

import org.jetbrains.annotations.Nullable;

public interface IrisApiCompat {
    static boolean isIrisLoaded() {
        return FabricLoader.getInstance().isModLoaded("iris");
    }

    static boolean areShadersEnabled() {
        return isIrisLoaded() && IrisApiCompatImpl.areShadersEnabled();
    }

    static boolean isOnShadowPass() {
        return isIrisLoaded() && IrisApiCompatImpl.isOnShadowPass();
    }

    static @Nullable RenderLayer wrapExactlyOnce(@Nullable RenderLayer layer) {
        if (layer == null || !isIrisLoaded()) {
            return layer;
        }

        return IrisApiCompatImpl.wrapExactlyOnce(layer);
    }

    static <T> T iris$capture(T object) {
        if (object == null || !isIrisLoaded()) {
            return object;
        }

        return IrisApiCompatImpl.iris$capture(object);
    }
}
