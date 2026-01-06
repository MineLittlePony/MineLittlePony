package com.minelittlepony.client.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.layer.BlockEntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.mixinterface.ModelStorage;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.render.RenderLayer;

import org.jetbrains.annotations.Nullable;

interface IrisApiCompatImpl {
    static boolean areShadersEnabled() {
        return IrisApi.getInstance().getConfig().areShadersEnabled();
    }

    static boolean isOnShadowPass() {
        return IrisApi.getInstance().isRenderingShadowPass();
    }

    static @Nullable RenderLayer wrapExactlyOnce(@Nullable RenderLayer layer) {
        return ImmediateState.isRenderingBEs ? OuterWrappedRenderType.wrapExactlyOnce("iris:block_entity", layer, BlockEntityRenderStateShard.INSTANCE) : layer;
    }

    static <T> T iris$capture(T object) {
        ((ModelStorage)object).iris$capture();
        return object;
    }
}
