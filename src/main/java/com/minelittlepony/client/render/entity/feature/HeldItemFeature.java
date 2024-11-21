package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;

@SuppressWarnings(value = {"unchecked"})
public class HeldItemFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, M> {

    private final PonyRenderContext<?, S, M> context;

    public HeldItemFeature(PonyRenderContext<?, S, M> context, ItemRenderer renderer) {
        super(context.upcast(), renderer);
        this.context = context;
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        if (!state.leftHandStack.isEmpty() || !state.rightHandStack.isEmpty()) {
            M model = context.getInternalRenderer().getModels().body();

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

    @Override
    public final void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        render(matrices, vertices, light, (S)state, limbAngle, limbDistance);
    }
}
