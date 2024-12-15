package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.mson.api.ModelKey;

// separate class in case I need it later
public class ClothingFeature<
        T extends LivingEntity,
        M extends ClientPonyModel<T>
    > extends FeatureRenderer<T, M> {

    protected final FeatureRendererContext<T, M> context;
    private final M model;
    private final Identifier texture;

    public ClothingFeature(FeatureRendererContext<T, M> context, ModelKey<? super M> model, Identifier texture) {
        super(context);
        this.context = context;
        this.model = model.createModel();
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        VertexConsumer buffer = vertices.getBuffer(model.getLayer(texture));
        model.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
    }
}