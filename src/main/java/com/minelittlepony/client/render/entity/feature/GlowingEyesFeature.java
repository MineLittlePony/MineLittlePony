package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class GlowingEyesFeature<
        S extends PonyRenderState,
        M extends EntityModel<PlayerEntityRenderState> & PonyModel<S>
    > extends EyesFeatureRenderer<PlayerEntityRenderState, M> {

    private final RenderLayer layer;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <V extends FeatureRendererContext & PonyRenderContext<?, S, M> & IGlowingRenderer> GlowingEyesFeature(V renderer) {
        super(renderer);
        layer = RenderLayer.getEyes(renderer.getEyeTexture());
    }

    @Override
    public RenderLayer getEyesTexture() {
        return layer;
    }

    public interface IGlowingRenderer {
        Identifier getEyeTexture();
    }
}
