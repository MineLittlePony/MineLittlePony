package com.minelittlepony.client.render.entity.feature;

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
import net.minecraft.util.Arm;

public class HeldItemFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, M> {

    public HeldItemFeature(PonyRenderContext<?, S, M> context) {
        super(context.upcast());
    }

    @SuppressWarnings(value = {"unchecked"})
    @Deprecated
    @Override
    public final void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        render(matrices, queue, light, (S)state, limbAngle, limbDistance);
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandItemState.isEmpty() || !state.rightHandItemState.isEmpty()) {
            renderItem(state, state.rightHandItemState, state.rightHeldItem, Arm.RIGHT, matrices, queue, light);
            renderItem(state, state.leftHandItemState, state.leftHeldItem, Arm.LEFT, matrices, queue, light);
        }
    }

    @SuppressWarnings(value = {"unchecked"})
    protected void renderItem(S state, ItemRenderState item, PonyRenderState.HeldItemRenderState glintLessItem, Arm arm, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        if (!item.isEmpty()) {
            matrices.push();
            if (getContextModel() instanceof AbstractPonyModel m) {
                m.transformHeldItem(state, arm, matrices);
            }

            renderItem((PlayerEntityRenderState)state, item, arm, matrices, queue, light);

            if (!glintLessItem.glintlessHandItemState.isEmpty()) {
                queue = MagicGlow.getQueue(state.glowColor, queue, LevitatingItemRenderer.getThirdPersonLevitatingItemTransformPasses(state, glintLessItem));
                renderItem((PlayerEntityRenderState)state, glintLessItem.glintlessHandItemState, arm, matrices, queue, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            }
            matrices.pop();
        }
    }
}
