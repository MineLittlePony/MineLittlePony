package com.minelittlepony.client.compat.iris;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;

public interface IrisApiCompat {
    static boolean areShadersEnabled() {
        if (!FabricLoader.getInstance().isModLoaded("iris")) {
            return false;
        }

        return IrisApi.getInstance().getConfig().areShadersEnabled();
    }

    static boolean isOnShadowPass() {
        if (!FabricLoader.getInstance().isModLoaded("iris")) {
            return false;
        }

        return IrisApi.getInstance().isRenderingShadowPass();
    }
}
