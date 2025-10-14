package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.RotationAxis;

public class LevitatingItemRenderer {
    /**
     * Renders a first-person item with a magical overlay.
     */
    public void renderItem(
            LivingEntity entity, ItemStack stack, ItemDisplayContext mode,
            ItemRenderState itemRenderState, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, int outline, Operation<Void> original) {

        if (!PonyConfig.getInstance().fpsmagic.get() || entity == null || !mode.isFirstPerson()) {
            original.call(itemRenderState, matrices, queue, light, overlay, outline);
            return;
        }

        var context = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity);
        var state = context == null ? null : context.getAndUpdateRenderState(entity, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false));

        if (state == null || !state.hasMagicGlow()) {
            original.call(itemRenderState, matrices, queue, light, overlay, outline);
            return;
        }

        var itemState = mode.isLeftHand() ? state.leftHeldItem : state.rightHeldItem;

        itemState.updateItemRenderState(state, MinecraftClient.getInstance().getItemModelManager(), stack, mode, entity);

        setupPerspective(state, itemState, mode.isLeftHand(), matrices);
        original.call(itemRenderState, matrices, queue, light, overlay, outline);

        if (state.hornGlowVisible) {
            queue = MagicGlow.getQueue(state.glowColor, queue);

            var box = itemState.glintlessHandItemState.getModelBoundingBox();

            float scale = itemState.levitatingItemScale;
            matrices.push();
            matrices.translate(0.015F + itemState.levitatingItemXDrift, 0.01F, 0.01F + itemState.levitatingItemZDrift);
            var dX = (box.maxX + box.minX) * 0.5;
            var dY = (box.maxY + box.minY) * 0.5;
            var dZ = (box.maxZ + box.minZ) * 0.5;

            matrices.translate(dX, dY, dZ);
            matrices.scale(scale, scale, scale);
            matrices.translate(-dX, -dY, -dZ);

            original.call(itemState.glintlessHandItemState, matrices, queue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
            matrices.translate(dX, dY, dZ);
            matrices.scale(scale, scale, scale);
            matrices.translate(-dX, -dY, -dZ);
            matrices.translate(-0.03F - itemState.levitatingItemXDrift, -0.02F, -0.02F - itemState.levitatingItemZDrift);
            original.call(itemState.glintlessHandItemState, matrices, queue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
            matrices.pop();
        }
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    public static void setupPerspective(PonyRenderState state, PonyRenderState.HeldItemRenderState heldItem, boolean left, MatrixStack stack) {
        if (heldItem.repositionFirstPerson) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = left ? 1 : -1;

            float floatAmount = heldItem.levitatingItemXDrift * 2.5F;
            float driftAmount = heldItem.levitatingItemZDrift * 4;
            float distanceChange = heldItem.handHeldTool ? -0.3F : -0.6F;

            stack.translate(driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign, floatAmount, distanceChange);

            if (!heldItem.handHeldTool) { // bows have to point forwards
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sign * -60 + floatAmount));
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sign * 30 + driftAmount));
            }
        }
    }
}
