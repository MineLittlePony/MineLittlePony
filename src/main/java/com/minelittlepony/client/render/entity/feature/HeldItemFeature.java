package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;

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
    public final void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        render(matrices, queue, light, (S)state, limbAngle, limbDistance);
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandItemState.isEmpty() || !state.rightHandItemState.isEmpty()) {
            M model = context.getEquineManager().getModels().body();

            matrices.push();
            model.transform(state, BodyPart.LEGS, matrices);
            renderItem(state, state.rightHandItemState, state.rightHeldItem, Arm.RIGHT, matrices, queue, light);
            renderItem(state, state.leftHandItemState, state.leftHeldItem, Arm.LEFT, matrices, queue, light);
            matrices.pop();
        }
    }

    @SuppressWarnings(value = {"unchecked"})
    protected void renderItem(S state, ItemRenderState item, PonyRenderState.HeldItemRenderState glintLessItem, Arm arm, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        if (!item.isEmpty()) {
            if (context.getEquineManager().getModels().body() instanceof AbstractPonyModel m) {
                m.positionheldItem(state, arm, matrices);
            }

            renderItem((PlayerEntityRenderState)state, item, arm, matrices, queue, light);

            if (!glintLessItem.glintlessHandItemState.isEmpty()) {
                queue = MagicGlow.getQueue(state.pony.metadata().glowColor(), queue);

                boolean noTransform = state.getHeldItem(arm).action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0;

                var box = glintLessItem.glintlessHandItemState.getModelBoundingBox();

                float scale = glintLessItem.levitatingItemScale;
                matrices.push();
                if (!noTransform) {
                    matrices.translate(0.03F, -0.12F, 0.02F);
                    matrices.translate(0.015F + glintLessItem.levitatingItemXDrift, 0.01F, 0.01F + glintLessItem.levitatingItemZDrift);
                }

                var dX = (box.maxX + box.minX) * 0.5;
                var dY = (box.maxY + box.minY) * 0.5;
                var dZ = (box.maxZ + box.minZ) * 0.5;

                matrices.translate(dX, dY, dZ);
                matrices.scale(scale, scale, scale);
                matrices.translate(-dX, -dY, -dZ);

                renderItem((PlayerEntityRenderState)state, glintLessItem.glintlessHandItemState, arm, matrices, queue, LightmapTextureManager.MAX_LIGHT_COORDINATE);
                matrices.translate(dX, dY, dZ);
                matrices.scale(scale, scale, scale);
                matrices.translate(-dX, -dY, -dZ);
                if (!noTransform) {
                    matrices.translate(0.1F, -0.1F, 0.1F);
                    matrices.translate(-0.03F - glintLessItem.levitatingItemXDrift, -0.02F, -0.02F - glintLessItem.levitatingItemZDrift);
                }

                renderItem((PlayerEntityRenderState)state, glintLessItem.glintlessHandItemState, arm, matrices, queue, LightmapTextureManager.MAX_LIGHT_COORDINATE);
                matrices.pop();
            }
        }
    }
}
