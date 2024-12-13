package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

public class HeldItemFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, M> {

    private final PonyRenderContext<?, S, M> context;

    public HeldItemFeature(PonyRenderContext<?, S, M> context, ItemRenderer renderer) {
        super(context.upcast(), renderer);
        this.context = context;
    }

    @SuppressWarnings(value = {"unchecked"})
    @Deprecated
    @Override
    public final void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        render(matrices, vertices, light, (S)state, limbAngle, limbDistance);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandStack.isEmpty() || !state.rightHandStack.isEmpty()) {
            M model = context.getEquineManager().getModels().body();

            matrices.push();
            model.transform(state, BodyPart.LEGS, matrices);

            ModelAttributes attributes = ((PonyModel.AttributedHolder)state).getAttributes();

            attributes.heldStack = state.rightHandStack;
            renderItem(state, state.rightHandItemModel, state.rightHandStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrices, vertices, light);
            attributes.heldStack = state.leftHandStack;
            renderItem(state, state.leftHandItemModel, state.leftHandStack, ModelTransformationMode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrices, vertices, light);
            attributes.heldStack = ItemStack.EMPTY;
            matrices.pop();
        }
    }

    @SuppressWarnings(value = {"unchecked"})
    protected void renderItem(S state, @Nullable BakedModel model, ItemStack item, ModelTransformationMode mode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        if (context.getEquineManager().getModels().body() instanceof AbstractPonyModel m) {
            m.positionheldItem(state, arm, matrices);
        }
        renderItem((PlayerEntityRenderState)state, model, item, mode, arm, matrices, vertices, light);

        if (PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow()) {
            vertices = LevitatingItemRenderer.getProvider(state.pony, vertices);

            if (item.hasGlint()) {
                item = item.copy();
                item.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            }

            boolean noTransform = state.attributes.heldStack.getUseAction() == UseAction.SPYGLASS && state.attributes.itemUseTime > 0;

            float driftStrength = 0.002F;
            float xDrift = MathHelper.sin(state.age / 10F) * driftStrength;
            float zDrift = MathHelper.cos((state.age + 20) / 10F) * driftStrength;

            float scale = 1.1F + (MathHelper.sin(state.age / 20F) + 1) * driftStrength;

            matrices.scale(scale, scale, scale);
            if (!noTransform) {
                matrices.translate(0.045F + xDrift, 0.01F - 0.12F, 0.03F + zDrift);
            }
            renderItem((PlayerEntityRenderState)state, model, item, mode, arm, matrices, vertices, light);
            if (!noTransform) {
                matrices.scale(scale, scale, scale);
                matrices.translate(0.1F, -0.1F, 0.1F);
                matrices.translate(-0.03F - xDrift, -0.02F, -0.02F - zDrift);
            }
            renderItem((PlayerEntityRenderState)state, model, item, mode, arm, matrices, vertices, light);
        }
    }
}
