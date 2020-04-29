package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.model.BodyPart;

public class PassengerFeature<T extends PlayerEntity, M extends ClientPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private final ParrotEntityModel model = new ParrotEntityModel();

    public PassengerFeature(IPonyRenderContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        renderShoulderParrot(stack, renderContext, lightUv, entity, limbDistance, limbAngle, headYaw, headPitch, true);
        renderShoulderParrot(stack, renderContext, lightUv, entity, limbDistance, limbAngle, headYaw, headPitch, false);
    }

    private void renderShoulderParrot(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float headYaw, float headPitch, boolean left) {

        CompoundTag riderTag = left ? entity.getShoulderEntityLeft() : entity.getShoulderEntityRight();

        EntityType.get(riderTag.getString("id")).filter(p -> p == EntityType.PARROT).ifPresent((entityType) -> {
           stack.push();

           getContextModel().transform(BodyPart.BODY, stack);

           stack.translate(left ? 0.25 : -0.25, entity.isInSneakingPose() ? -0.5 : -0.25, 0.35);
           stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(left ? -5 : 5));

           VertexConsumer vertexConsumer = renderContext.getBuffer(model.getLayer(ParrotEntityRenderer.TEXTURES[riderTag.getInt("Variant")]));
           model.poseOnShoulder(stack, vertexConsumer, lightUv, OverlayTexture.DEFAULT_UV, limbDistance, limbAngle, headYaw, headPitch, entity.age);
           stack.pop();
        });
    }
}
