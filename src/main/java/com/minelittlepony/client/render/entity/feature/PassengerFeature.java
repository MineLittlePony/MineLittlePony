package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRenderContext;

import java.util.Optional;

public class PassengerFeature<T extends PlayerEntity, M extends ClientPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private final ParrotEntityModel model;

    public PassengerFeature(IPonyRenderContext<T, M> renderer, EntityRendererFactory.Context context) {
        super(renderer);
        model = new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT));
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        getShoulderParrot(entity.getShoulderEntityLeft()).ifPresent(texture -> {
            renderShoulderParrot(stack, renderContext, light, entity, limbDistance, limbAngle, headYaw, headPitch, texture, 1);
        });
        getShoulderParrot(entity.getShoulderEntityRight()).ifPresent(texture -> {
            renderShoulderParrot(stack, renderContext, light, entity, limbDistance, limbAngle, headYaw, headPitch, texture, -1);
        });
    }

    private Optional<Identifier> getShoulderParrot(NbtCompound tag) {
        return EntityType.get(tag.getString("id"))
                .filter(p -> p == EntityType.PARROT)
                .map(type -> ParrotEntityRenderer.TEXTURES[tag.getInt("Variant")]);
    }

    private void renderShoulderParrot(MatrixStack stack, VertexConsumerProvider renderContext, int light, T entity, float limbDistance, float limbAngle, float headYaw, float headPitch, Identifier texture, int sigma) {
       stack.push();

       getContextModel().transform(BodyPart.BODY, stack);

       stack.translate(
               sigma * 0.25,
               entity.isInSneakingPose() ? -0.9 : -1.2,
               0.45);
       stack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(sigma * -5));

       VertexConsumer buffer = renderContext.getBuffer(model.getLayer(texture));
       model.poseOnShoulder(stack, buffer, light, OverlayTexture.DEFAULT_UV, limbDistance, limbAngle, headYaw, headPitch, entity.age);
       stack.pop();
    }
}
