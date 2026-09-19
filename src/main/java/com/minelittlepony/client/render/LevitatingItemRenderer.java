package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.HeldItemRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LevitatingItemRenderer {
    private static final Identifier MAP_BACKGROUND = Identifier.withDefaultNamespace("textures/map/map_background.png");
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
            PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState armState, ItemStack stack, InteractionHand hand,
            ItemStackRenderState itemRenderState, PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, int outline, Operation<Void> original) {

        if (!PonyConfig.getInstance().fpsmagic.get() || !(playerState.avatarRenderState instanceof PonyRenderState state) || !state.hasMagicGlow()) {
            original.call(itemRenderState, matrices, frame, light, overlay, outline);
            return;
        }

        var arm = hand.asArm(state.mainArm);

        var itemState = state.getHeldItem(arm);

        setupPerspective(state, itemState, arm == HumanoidArm.LEFT, true, matrices);
        original.call(itemRenderState, matrices, frame, light, overlay, outline);

        if (state.hornGlowVisible && !itemState.glintlessHandItemState.isEmpty()) {
            var q = MagicGlow.getQueue(state.glowColor, frame, calculateTransformPasses(itemState, FIRST_PERSON_TRANSFORM, false));
            original.call(itemRenderState, matrices, q, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    public static void renderMap(PoseStack matrices, SubmitNodeCollector queue, ItemStack stack, boolean mainHand, FirstPersonHandsAndItemsRenderState handsState) {

        if (!(Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.playerRenderState.avatarRenderState instanceof PonyRenderState state)
                || !PonyConfig.getInstance().fpsmagic.get() || !state.hasMagicGlow()) {
            return;
        }

        HumanoidArm arm = mainHand ? state.mainArm : state.mainArm.getOpposite();
        HeldItemRenderState itemState = state.getHeldItem(arm);

        float floatAmount = itemState.levitatingItemXDrift * 2000;
        float driftAmount = itemState.levitatingItemZDrift * 2000;

        matrices.translate(floatAmount, driftAmount, driftAmount * 0.25F);

        RenderType renderLayer = MagicGlow.getTextured(MAP_BACKGROUND);
        for (var pass : calculateTransformPasses(itemState, FIRST_PERSON_TRANSFORM, false)) {
            matrices.pushPose();
            matrices.last().pose().scaleAround(1 + pass.scale() / 3F, 64, 64, 0);
            matrices.translate(pass.translation());

            queue.submitCustomGeometry(matrices, renderLayer, (entry, buffer) -> {
                buffer.addVertex(entry, -7, 135, 1).setColor(state.glowColor).setUv(0, 1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
                buffer.addVertex(entry, 135, 135, 1).setColor(state.glowColor).setUv(1, 1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
                buffer.addVertex(entry, 135, -7, 1).setColor(state.glowColor).setUv(1, 0).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
                buffer.addVertex(entry, -7, -7, 1).setColor(state.glowColor).setUv(0, 0).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 1, 0);
            });
            matrices.popPose();
        }
    }

    public static ArrayList<MagicOverlayRenderCommandQueue.Pass> getThirdPersonLevitatingItemTransformPasses(PonyRenderState state, PonyRenderState.HeldItemRenderState glintLessItem) {
        return calculateTransformPasses(glintLessItem, THIRD_PERSON_TRANSFORM, glintLessItem.action == ItemUseAnimation.SPYGLASS && state.attributes.itemUseTime > 0);
    }

    private static ArrayList<MagicOverlayRenderCommandQueue.Pass> calculateTransformPasses(PonyRenderState.HeldItemRenderState glintLessItem, Vector3fc[] offset, boolean noTransform) {
        var passes = new ArrayList<MagicOverlayRenderCommandQueue.Pass>();
        passes.add(new MagicOverlayRenderCommandQueue.Pass(
                noTransform ? Vec3.ZERO : new Vec3(offset[0].x() + glintLessItem.levitatingItemXDrift, offset[0].y(), offset[0].z() + glintLessItem.levitatingItemZDrift),
                glintLessItem.levitatingItemScale
        ));
        passes.add(new MagicOverlayRenderCommandQueue.Pass(
                noTransform ? Vec3.ZERO : new Vec3(offset[1].x() + glintLessItem.levitatingItemXDrift, offset[1].y(), offset[1].z() + glintLessItem.levitatingItemZDrift),
                glintLessItem.levitatingItemScale * 1.5F
        ));
        return passes;
    }


    /**
     * Moves held items to look like they're floating in the player's field.
     */
    public static void setupPerspective(PonyRenderState state, PonyRenderState.HeldItemRenderState heldItem, boolean left, boolean rotate, PoseStack stack) {
        if (heldItem.repositionFirstPerson) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = rotate ? (left ? 1 : -1) : 0;

            float floatAmount = heldItem.levitatingItemXDrift * 2.5F;
            float driftAmount = heldItem.levitatingItemZDrift * 4;
            float distanceChange = heldItem.handHeldTool ? -0.3F : -0.6F;

            stack.translate(driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign, floatAmount, distanceChange);

            if (rotate && !heldItem.handHeldTool) { // bows have to point forwards
                stack.rotateDegrees(Axis.YP, sign * -60 + floatAmount);
                stack.rotateDegrees(Axis.ZP, sign * 30 + driftAmount);
            }
        }
    }
}
