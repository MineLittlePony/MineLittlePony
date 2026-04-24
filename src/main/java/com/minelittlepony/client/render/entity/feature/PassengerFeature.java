package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.animal.parrot.Parrot;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class PassengerFeature<
        T extends Avatar & ClientAvatarEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final ParrotModel model;
    private final ParrotRenderState parrotState = new ParrotRenderState();

    public PassengerFeature(PonyRenderContext<T, S, M> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        model = new ParrotModel(context.bakeLayer(ModelLayers.PARROT));
        parrotState.pose = ParrotModel.Pose.ON_SHOULDER;
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, S state, float limbAngle, float limbDistance) {
        if (state.parrotOnLeftShoulder != null) {
            render(matrices, frame, light, state, state.parrotOnLeftShoulder, limbAngle, limbDistance, true);
        }

        if (state.parrotOnRightShoulder != null) {
            render(matrices, frame, light, state, state.parrotOnRightShoulder, limbAngle, limbDistance, false);
        }
    }

    private void render(
        PoseStack matrices,
        SubmitNodeCollector frame,
        int light,
        S state,
        Parrot.Variant parrotVariant,
        float headYaw,
        float headPitch,
        boolean left
    ) {
        matrices.pushPose();

        float scale = 1/state.attributes.size.scaleFactor();
        final double parrotModelHeight = 1.5;

        getParentModel().transformAccessory(state, BodyPart.BACK, matrices);

        matrices.translate(0, -1.28, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(left ? -5 : 5));
        matrices.translate(0, parrotModelHeight, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(left ? 0.25 : -0.25, -parrotModelHeight, 0.45);

        parrotState.ageInTicks = state.ageInTicks;
        parrotState.walkAnimationPos = state.walkAnimationPos;
        parrotState.walkAnimationSpeed = state.walkAnimationSpeed;
        parrotState.yRot = headYaw;
        parrotState.xRot = headPitch;
        frame.order(0).submitModel(model, parrotState, matrices, model.renderType(ParrotRenderer.getVariantTexture(parrotVariant)), light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        matrices.popPose();
    }
}
