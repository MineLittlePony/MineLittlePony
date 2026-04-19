package com.minelittlepony.client.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.layer.BlockEntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.rendertype.RenderType;

import org.jetbrains.annotations.Nullable;

interface IrisApiCompatImpl {
    static boolean areShadersEnabled() {
        return IrisApi.getInstance().getConfig().areShadersEnabled();
    }

    static boolean isOnShadowPass() {
        return IrisApi.getInstance().isRenderingShadowPass();
    }

    static @Nullable RenderType wrapExactlyOnce(@Nullable RenderType layer) {
        return ImmediateState.isRenderingBEs ? OuterWrappedRenderType.wrapExactlyOnce("iris:block_entity", layer, BlockEntityRenderStateShard.INSTANCE) : layer;
    }
}
