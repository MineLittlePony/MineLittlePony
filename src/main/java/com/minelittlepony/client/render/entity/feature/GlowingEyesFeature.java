package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.PonyRenderContext;

public class GlowingEyesFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends EyesFeatureRenderer<T, M> {

    private final RenderLayer layer;

    public <V extends FeatureRendererContext<T, M> & PonyRenderContext<T, M> & IGlowingRenderer> GlowingEyesFeature(V renderer) {
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
