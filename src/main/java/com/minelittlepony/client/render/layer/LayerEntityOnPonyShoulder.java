package com.minelittlepony.client.render.layer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;
import com.mojang.blaze3d.platform.GlStateManager;

import javax.annotation.Nullable;

public class LayerEntityOnPonyShoulder<M extends ClientPonyModel<AbstractClientPlayerEntity>> extends AbstractPonyLayer<AbstractClientPlayerEntity, M> {

    private final EntityRenderDispatcher renderManager;

    private LivingEntity leftEntity;
    private LivingEntity rightEntity;

    public LayerEntityOnPonyShoulder(EntityRenderDispatcher manager, IPonyRender<AbstractClientPlayerEntity, M> context) {
        super(context);
        renderManager = manager;
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1, 1, 1, 1);

        CompoundTag leftTag = player.getShoulderEntityLeft();

        if (!leftTag.isEmpty()) {
            leftEntity = renderShoulderEntity(player, leftEntity, leftTag, headYaw, headPitch, true);
        }

        CompoundTag rightTag = player.getShoulderEntityRight();

        if (!rightTag.isEmpty()) {
            rightEntity = renderShoulderEntity(player, rightEntity, rightTag, headYaw, headPitch, false);
        }

        GlStateManager.disableRescaleNormal();
    }

    @Nullable
    private LivingEntity renderShoulderEntity(AbstractClientPlayerEntity player, @Nullable LivingEntity entity, CompoundTag shoulderTag, float headYaw, float headPitch, boolean left) {

        if (entity == null || !entity.getUuid().equals(shoulderTag.getUuid("UUID"))) {
            entity = (LivingEntity) EntityType.getEntityFromTag(shoulderTag, player.world).orElse(null);
            // this isn't an entity.
            if (entity == null) {
                return null;
            }
        }

        EntityRenderer<LivingEntity> render = renderManager.getRenderer(entity);

        if (render == null) {
            return entity;
        }

        GlStateManager.pushMatrix();

        getModel().transform(BodyPart.BODY);

        // render on the haunches
        GlStateManager.translatef(left ? 0.25F : -0.25F, 0.25F, 0.35F);
        GlStateManager.scalef(1, -1, -1);
        GlStateManager.rotatef(left ? -5 : 5, 0, 0, 1);

        // look where the player is looking
        entity.prevHeadYaw = headYaw;
        entity.headYaw = headYaw;
        entity.pitch = headPitch;
        entity.prevPitch = headPitch;

        render.render(entity, 0, 0, 0, 0, 0);

        GlStateManager.popMatrix();
        return entity;
    }
}
