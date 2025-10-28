package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.HeldItemRenderState;

import java.util.ArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LevitatingItemRenderer {
    private static final Vector3fc[] THIRD_PERSON_TRANSFORM = {
            new Vector3f(-0.085F, 0.01F, -0.08F), new Vector3f(-0.035F, 0.01F, -0.14F)
    };
    private static final Vector3fc[] FIRST_PERSON_TRANSFORM = {
            new Vector3f(-0.035F, -0.11F, -0.09F), new Vector3f(-0.085F, -0.04F, -0.09F)
    };

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

        setupPerspective(state, itemState, mode.isLeftHand(), true, matrices);
        original.call(itemRenderState, matrices, queue, light, overlay, outline);

        if (state.hornGlowVisible) {
            var q = MagicGlow.getQueue(state.glowColor, queue, calculateTransformPasses(itemState, FIRST_PERSON_TRANSFORM, false));
            original.call(itemState.glintlessHandItemState, matrices, q, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
        }
    }

    public static void renderMap(MatrixStack matrices, OrderedRenderCommandQueue queue, int swingProgress, ItemStack stack) {

        if (!PonyConfig.getInstance().fpsmagic.get()) {
            return;
        }

        var entity = MinecraftClient.getInstance().player;
        var context = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity);
        var state = context == null ? null : context.getAndUpdateRenderState(entity, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false));

        if (state == null || !state.hasMagicGlow()) {
            return;
        }

        Arm arm = entity.getStackInArm(Arm.LEFT) == stack ? Arm.LEFT : Arm.RIGHT;
        HeldItemRenderState itemState = state.getHeldItem(arm);

        float floatAmount = itemState.levitatingItemXDrift * 2000;
        float driftAmount = itemState.levitatingItemZDrift * 2000;

        matrices.translate(floatAmount, driftAmount, driftAmount * 0.25F);

        RenderLayer renderLayer = MagicGlow.getTextured(Identifier.ofVanilla("textures/map/map_background.png"));
        for (var pass : calculateTransformPasses(itemState, FIRST_PERSON_TRANSFORM, false)) {
            matrices.push();
            matrices.peek().getPositionMatrix().scaleAround(1 + pass.scale() / 3F, 64, 64, 0);
            matrices.translate(pass.translation());

            queue.submitCustom(matrices, renderLayer, (entry, buffer) -> {
                buffer.vertex(entry, -7, 135, 1).color(state.glowColor).texture(0, 1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0);
                buffer.vertex(entry, 135, 135, 1).color(state.glowColor).texture(1, 1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0);
                buffer.vertex(entry, 135, -7, 1).color(state.glowColor).texture(1, 0).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0);
                buffer.vertex(entry, -7, -7, 1).color(state.glowColor).texture(0, 0).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).overlay(OverlayTexture.DEFAULT_UV).normal(0, 1, 0);
            });
            matrices.pop();
        }
    }

    public static ArrayList<MagicOverlayRenderCommandQueue.Pass> getThirdPersonLevitatingItemTransformPasses(PonyRenderState state, PonyRenderState.HeldItemRenderState glintLessItem) {
        return calculateTransformPasses(glintLessItem, THIRD_PERSON_TRANSFORM, glintLessItem.action == UseAction.SPYGLASS && state.attributes.itemUseTime > 0);
    }

    private static ArrayList<MagicOverlayRenderCommandQueue.Pass> calculateTransformPasses(PonyRenderState.HeldItemRenderState glintLessItem, Vector3fc[] offset, boolean noTransform) {
        var passes = new ArrayList<MagicOverlayRenderCommandQueue.Pass>();
        passes.add(new MagicOverlayRenderCommandQueue.Pass(
                noTransform ? Vec3d.ZERO : new Vec3d(offset[0].x() + glintLessItem.levitatingItemXDrift, offset[0].y(), offset[0].z() + glintLessItem.levitatingItemZDrift),
                glintLessItem.levitatingItemScale
        ));
        passes.add(new MagicOverlayRenderCommandQueue.Pass(
                noTransform ? Vec3d.ZERO : new Vec3d(offset[1].x() + glintLessItem.levitatingItemXDrift, offset[1].y(), offset[1].z() + glintLessItem.levitatingItemZDrift),
                glintLessItem.levitatingItemScale * 1.5F
        ));
        return passes;
    }


    /**
     * Moves held items to look like they're floating in the player's field.
     */
    public static void setupPerspective(PonyRenderState state, PonyRenderState.HeldItemRenderState heldItem, boolean left, boolean rotate, MatrixStack stack) {
        if (heldItem.repositionFirstPerson) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = rotate ? (left ? 1 : -1) : 0;

            float floatAmount = heldItem.levitatingItemXDrift * 2.5F;
            float driftAmount = heldItem.levitatingItemZDrift * 4;
            float distanceChange = heldItem.handHeldTool ? -0.3F : -0.6F;

            stack.translate(driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign, floatAmount, distanceChange);

            if (rotate && !heldItem.handHeldTool) { // bows have to point forwards
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sign * -60 + floatAmount));
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sign * 30 + driftAmount));
            }
        }
    }
}
