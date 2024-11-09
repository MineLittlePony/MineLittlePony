package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

// separate class in case I need it later
public abstract class AbstractClothingFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends BipedEntityModel<S> & PonyModel<S>
    > extends FeatureRenderer<S, M> {

    protected final FeatureRendererContext<S, M> renderer;

    public AbstractClothingFeature(FeatureRendererContext<S, M> render) {
        super(render);
        renderer = render;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        M overlayModel = getOverlayModel();

        overlayModel.setAngles(state);
        VertexConsumer buffer = vertices.getBuffer(overlayModel.getLayer(getOverlayTexture()));
        overlayModel.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
    }

    protected abstract M getOverlayModel();

    protected abstract Identifier getOverlayTexture();
}