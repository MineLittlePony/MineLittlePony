package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.IModel;

// separate class in case I need it later
public abstract class AbstractClothingFeature<T extends LivingEntity, M extends BipedEntityModel<T> & IModel> extends FeatureRenderer<T, M> {

    protected final FeatureRendererContext<T, M> renderer;

    public AbstractClothingFeature(FeatureRendererContext<T, M> render) {
        super(render);
        renderer = render;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        M overlayModel = getOverlayModel();

        renderer.getModel().copyStateTo(overlayModel);
        overlayModel.animateModel(entity, limbDistance, limbAngle, tickDelta);
        overlayModel.setAngles(entity, limbDistance, limbAngle, age, headYaw, headPitch);

        VertexConsumer vertexConsumer = renderContext.getBuffer(overlayModel.getLayer(getOverlayTexture()));
        overlayModel.render(stack, vertexConsumer, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    protected abstract M getOverlayModel();

    protected abstract Identifier getOverlayTexture();

}