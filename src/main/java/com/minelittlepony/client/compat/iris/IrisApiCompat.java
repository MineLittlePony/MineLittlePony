package com.minelittlepony.client.compat.iris;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.rendertype.RenderType;

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

    static @Nullable RenderType wrapExactlyOnce(@Nullable RenderType layer) {
        if (layer == null || !isIrisLoaded()) {
            return layer;
        }

        return IrisApiCompatImpl.wrapExactlyOnce(layer);
    }
}
