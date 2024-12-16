package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class GlowingEyesFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends EyesFeatureRenderer<PlayerEntityRenderState, M> {

    private final RenderLayer layer;

    public GlowingEyesFeature(PonyRenderContext<?, S, M> context, Identifier texture) {
        super(context.upcast());
        layer = RenderLayer.getEyes(texture);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return layer;
    }
}
