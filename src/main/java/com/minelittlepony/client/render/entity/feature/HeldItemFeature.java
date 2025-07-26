package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.MagicGlow;
import com.minelittlepony.client.render.PonyRenderContext;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

public class HeldItemFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T> & ModelWithArms> extends HeldItemFeatureRenderer<T, M> {

    private final PonyRenderContext<T, M> context;

    @SuppressWarnings("unchecked")
    public HeldItemFeature(PonyRenderContext<T, M> context, HeldItemRenderer renderer) {
        super((FeatureRendererContext<T, M>)context, renderer);
        this.context = context;
    }

    protected ItemStack getLeftItem(T entity) {
        boolean main = entity.getMainArm() == Arm.LEFT;

        return main ? entity.getMainHandStack() : entity.getOffHandStack();
    }

    protected ItemStack getRightItem(T entity) {
        boolean main = entity.getMainArm() == Arm.RIGHT;

        return main ? entity.getMainHandStack() : entity.getOffHandStack();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {

        ItemStack left = getLeftItem(entity);
        ItemStack right = getRightItem(entity);

        if (!left.isEmpty() || !right.isEmpty()) {
            M model = context.getInternalRenderer().getModels().body();

            vertices = MagicGlow.getProvider(context.getEntityPony(entity).metadata().glowColor(), vertices, matrices);

            matrices.push();

            model.transform(BodyPart.LEGS, matrices);

            model.getAttributes().heldStack = right;
            renderItem(entity, right, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrices, vertices, lightUv);
            model.getAttributes().heldStack = left;
            renderItem(entity, left, ModelTransformationMode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrices, vertices, lightUv);
            model.getAttributes().heldStack = ItemStack.EMPTY;
            matrices.pop();
        }
    }
}
