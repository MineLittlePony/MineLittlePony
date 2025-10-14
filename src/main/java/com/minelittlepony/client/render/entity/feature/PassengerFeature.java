package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.passive.ParrotEntity;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PassengerFeature<
        T extends PlayerLikeEntity & ClientPlayerLikeEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final ParrotEntityModel model;
    private final ParrotEntityRenderState parrotState = new ParrotEntityRenderState();

    public PassengerFeature(PonyRenderContext<T, S, M> renderer, EntityRendererFactory.Context context) {
        super(renderer);
        model = new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT));
        parrotState.parrotPose = ParrotEntityModel.Pose.ON_SHOULDER;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (state.leftShoulderParrotVariant != null) {
            render(matrices, queue, light, state, state.leftShoulderParrotVariant, limbAngle, limbDistance, true);
        }

        if (state.rightShoulderParrotVariant != null) {
            render(matrices, queue, light, state, state.rightShoulderParrotVariant, limbAngle, limbDistance, false);
        }
    }

    private void render(
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        S state,
        ParrotEntity.Variant parrotVariant,
        float headYaw,
        float headPitch,
        boolean left
    ) {
        matrices.push();

        float scale = 1/state.attributes.size.scaleFactor();
        final double parrotModelHeight = 1.5;

        getContextModel().transform(state, BodyPart.BACK, matrices);
        getContextModel().body.applyTransform(matrices);

        matrices.translate(0, -1.28, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(left ? -5 : 5));
        matrices.translate(0, parrotModelHeight, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(left ? 0.25 : -0.25, -parrotModelHeight, 0.45);

        parrotState.age = state.age;
        parrotState.limbSwingAnimationProgress = state.limbSwingAnimationProgress;
        parrotState.limbSwingAmplitude = state.limbSwingAmplitude;
        parrotState.relativeHeadYaw = headYaw;
        parrotState.pitch = headPitch;
        queue.getBatchingQueue(0).submitModel(model, parrotState, matrices, model.getLayer(ParrotEntityRenderer.getTexture(parrotVariant)), light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        matrices.pop();
    }
}
