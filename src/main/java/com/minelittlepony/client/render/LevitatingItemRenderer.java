package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LevitatingItemRenderer {
    private static final Vector3fc[] THIRD_PERSON_TRANSFORM = {
            new Vector3f(-0.1F, 0, -0.09F), new Vector3f(-0.05F, 0, -0.15F)
    };
    private static final Vector3fc[] FIRST_PERSON_TRANSFORM = {
            new Vector3f(-0.05F, -0.12F, -0.1F), new Vector3f(-0.1F, -0.05F, -0.1F)
    };
    private static final MatrixStack TRANSFORM = new MatrixStack();


    /**
     * Renders a first-person item with a magical overlay.
     */
    public static void renderItem(
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
            var q = MagicGlow.getQueue(state.glowColor, queue, calculateTransformPasses(itemState, FIRST_PERSON_TRANSFORM, false));
            original.call(itemState.glintlessHandItemState, matrices, q, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
        }
    }

    public static ArrayList<MagicOverlayRenderCommandQueue.Pass> getThirdPersonLevitatingItemTransformPasses(PonyRenderState state, PonyRenderState.HeldItemRenderState glintLessItem) {
        return calculateTransformPasses(glintLessItem, THIRD_PERSON_TRANSFORM, glintLessItem.action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0);
    }

    private static ArrayList<MagicOverlayRenderCommandQueue.Pass> calculateTransformPasses(PonyRenderState.HeldItemRenderState glintLessItem, Vector3fc[] offset, boolean noTransform) {
        var passes = new ArrayList<MagicOverlayRenderCommandQueue.Pass>();

        var box = glintLessItem.glintlessHandItemState.getModelBoundingBox();

        float scale = glintLessItem.levitatingItemScale;
        scale = 1 + (scale - 1) * 2;

        var dX = (box.maxX + box.minX) * 0.5;
        var dY = (box.maxY + box.minY) * 0.5;
        var dZ = (box.maxZ + box.minZ) * 0.5;

        TRANSFORM.peek().loadIdentity();
        Vec3d translation = Vec3d.ZERO;
        if (!noTransform) {
            translation = new Vec3d(offset[0].x() + 0.015F + glintLessItem.levitatingItemXDrift, offset[0].y() + 0.01F, offset[0].z() + 0.01F + glintLessItem.levitatingItemZDrift);
            TRANSFORM.translate(translation);
        }
        TRANSFORM.translate(dX, dY, dZ);
        TRANSFORM.scale(scale, scale, scale);
        TRANSFORM.translate(-dX, -dY, -dZ);

        passes.add(new MagicOverlayRenderCommandQueue.Pass(TRANSFORM.peek().copy(), translation, scale));

        translation = Vec3d.ZERO;
        scale = 1 + (scale - 1) * 1.5F;
        TRANSFORM.peek().loadIdentity();
        TRANSFORM.translate(dX, dY, dZ);
        TRANSFORM.scale(scale, scale, scale);
        TRANSFORM.translate(-dX, -dY, -dZ);
        if (!noTransform) {
            translation = new Vec3d(offset[1].x() + 0.015F + glintLessItem.levitatingItemXDrift, offset[1].y() + 0.01F, offset[1].z() + 0.01F + glintLessItem.levitatingItemZDrift);
            TRANSFORM.translate(translation);
        }

        passes.add(new MagicOverlayRenderCommandQueue.Pass(TRANSFORM.peek().copy(), translation, scale));
        return passes;
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
