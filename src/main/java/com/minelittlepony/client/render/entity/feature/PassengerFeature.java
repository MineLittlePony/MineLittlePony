package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PassengerFeature<
        T extends PlayerEntity,
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
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        if (state.leftShoulderParrotVariant != null) {
            render(matrices, vertices, light, state, state.leftShoulderParrotVariant, limbAngle, limbDistance, true);
        }

        if (state.rightShoulderParrotVariant != null) {
            render(matrices, vertices, light, state, state.rightShoulderParrotVariant, limbAngle, limbDistance, false);
        }
    }

    private void render(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        S state,
        ParrotEntity.Variant parrotVariant,
        float headYaw,
        float headPitch,
        boolean left
    ) {
        matrices.push();
        getContextModel().transform(state, BodyPart.BACK, matrices);
        matrices.translate(
                left ? 0.25F : -0.25F,
                state.isInSneakingPose ? -1.3F : -1.5F, 0.0F
        );
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(left ? -5 : 5));
        parrotState.age = state.age;
        parrotState.limbFrequency = state.limbFrequency;
        parrotState.limbAmplitudeMultiplier = state.limbAmplitudeMultiplier;
        parrotState.yawDegrees = headYaw;
        parrotState.pitch = headPitch;
        model.setAngles(parrotState);
        model.render(matrices, vertexConsumers.getBuffer(model.getLayer(ParrotEntityRenderer.getTexture(parrotVariant))), light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
