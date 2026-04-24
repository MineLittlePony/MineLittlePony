package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class HeldItemFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends PlayerItemInHandLayer<AvatarRenderState, M> {

    public HeldItemFeature(PonyRenderContext<?, S, M> context) {
        super(context.upcast());
    }

    @Deprecated
    @Override
    public final void submit(PoseStack matrices, SubmitNodeCollector frame, int light, AvatarRenderState state, float limbAngle, float limbDistance) {
        render(matrices, frame, light, Untyped.cast(state), limbAngle, limbDistance);
    }

    public void render(PoseStack matrices, SubmitNodeCollector frame, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandItemState.isEmpty() || !state.rightHandItemState.isEmpty()) {
            renderItem(state, state.rightHandItemState, state.rightHandItemStack, state.rightHeldItem, HumanoidArm.RIGHT, matrices, frame, light);
            renderItem(state, state.leftHandItemState, state.leftHandItemStack, state.leftHeldItem, HumanoidArm.LEFT, matrices, frame, light);
        }
    }

    protected void renderItem(S state, ItemStackRenderState item, ItemStack stack, PonyRenderState.HeldItemRenderState glintLessItem, HumanoidArm arm, PoseStack matrices, SubmitNodeCollector queue, int light) {
        if (item.isEmpty()) {
            return;
        }

        matrices.pushPose();

        submitArmWithItem(state, item, stack, arm, matrices, queue, light);

        if (PonyConfig.getInstance().tpsmagic.get() && !glintLessItem.glintlessHandItemState.isEmpty() && !IrisApiCompat.isOnShadowPass()) {
            queue = MagicGlow.getQueue(state.glowColor, queue, LevitatingItemRenderer.getThirdPersonLevitatingItemTransformPasses(state, glintLessItem));
            submitArmWithItem(state, glintLessItem.glintlessHandItemState, stack, arm, matrices, queue, LightCoordsUtil.FULL_BRIGHT);
        }
        matrices.popPose();
    }

    @Override
    protected void renderItemHeldToEye(AvatarRenderState state, HumanoidArm arm, PoseStack matrices, SubmitNodeCollector frame, int light) {
        getParentModel().transformHeldItem(Untyped.cast(state), arm, matrices);
        super.renderItemHeldToEye(state, arm, matrices, frame, light);
    }
}
