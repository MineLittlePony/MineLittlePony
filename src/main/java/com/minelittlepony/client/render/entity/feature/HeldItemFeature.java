package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class HeldItemFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, M> {

    private final PonyRenderContext<?, S, M> context;

    public HeldItemFeature(PonyRenderContext<?, S, M> context) {
        super(context.upcast());
        this.context = context;
    }

    @SuppressWarnings(value = {"unchecked"})
    @Deprecated
    @Override
    public final void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        render(matrices, vertices, light, (S)state, limbAngle, limbDistance);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandItemState.isEmpty() || !state.rightHandItemState.isEmpty()) {
            M model = context.getEquineManager().getModels().body();

            matrices.push();
            model.transform(state, BodyPart.LEGS, matrices);
            renderItem(state, state.rightHandItemState, state.glintlessRightHandItemState, Arm.RIGHT, matrices, vertices, light);
            renderItem(state, state.leftHandItemState, state.glintlessLeftHandItemState, Arm.LEFT, matrices, vertices, light);
            matrices.pop();
        }
    }

    @SuppressWarnings(value = {"unchecked"})
    protected void renderItem(S state, ItemRenderState item, ItemRenderState glintLessItem, Arm arm, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (!item.isEmpty()) {
            if (context.getEquineManager().getModels().body() instanceof AbstractPonyModel m) {
                m.positionheldItem(state, arm, matrices);
            }
            renderItem((PlayerEntityRenderState)state, item, arm, matrices, vertices, light);

            if (!glintLessItem.isEmpty()) {
                vertices = LevitatingItemRenderer.getProvider(state.pony, vertices);

                boolean noTransform = state.getHeldItem(arm).action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0;

                float driftStrength = 0.002F;
                float xDrift = MathHelper.sin(state.age / 10F) * driftStrength;
                float zDrift = MathHelper.cos((state.age + 20) / 10F) * driftStrength;

                float scale = 1.1F + (MathHelper.sin(state.age / 20F) + 1) * driftStrength;

                matrices.scale(scale, scale, scale);
                if (!noTransform) {
                    matrices.translate(0.045F + xDrift, 0.01F - 0.12F, 0.03F + zDrift);
                }
                renderItem((PlayerEntityRenderState)state, glintLessItem, arm, matrices, vertices, light);
                if (!noTransform) {
                    matrices.scale(scale, scale, scale);
                    matrices.translate(0.1F, -0.1F, 0.1F);
                    matrices.translate(-0.03F - xDrift, -0.02F, -0.02F - zDrift);
                }
                renderItem((PlayerEntityRenderState)state, glintLessItem, arm, matrices, vertices, light);
            }
        }
    }
}
