package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class LevitatingItemRenderer {
    /**
     * Renders a first-person item with a magical overlay.
     */
    public boolean renderItem(ItemRenderer itemRenderer, @Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext mode,
            MatrixStack matrices, VertexConsumerProvider vertices, @Nullable World world,
            int light, int overlay, int seed, Operation<Void> original) {

        if (!PonyConfig.getInstance().fpsmagic.get() || entity == null || !mode.isFirstPerson()) {
            return false;
        }

        var context = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity);
        if (context == null) {
            return false;
        }

        var state = context.getAndUpdateRenderState(entity, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false));

        if (!state.hasMagicGlow()) {
            return false;
        }

        var itemState = mode.isLeftHand() ? state.leftHeldItem : state.rightHeldItem;

        itemState.updateItemRenderState(state, MinecraftClient.getInstance().getItemModelManager(), stack, mode, entity);

        setupPerspective(state, itemState, stack, mode.isLeftHand(), matrices);
        original.call(itemRenderer, entity, stack, mode, matrices, vertices, world, light, overlay, seed);

        VertexConsumerProvider interceptedContext = MagicGlow.getProvider(state.pony.metadata().glowColor(), vertices, matrices);

        @Nullable
        Boolean glint = stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
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

        original.call(itemRenderer, entity, stack, mode, matrices, interceptedContext, world, light, OverlayTexture.DEFAULT_UV, seed);
        matrices.translate(dX, dY, dZ);
        matrices.scale(scale, scale, scale);
        matrices.translate(-dX, -dY, -dZ);
        matrices.translate(-0.03F - itemState.levitatingItemXDrift, -0.02F, -0.02F - itemState.levitatingItemZDrift);
        original.call(itemRenderer, entity, stack, mode, matrices, interceptedContext, world, light, OverlayTexture.DEFAULT_UV, seed);
        matrices.pop();

        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return true;
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    public static void setupPerspective(PonyRenderState state, PonyRenderState.HeldItemRenderState heldItem, ItemStack item, boolean left, MatrixStack stack) {
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
