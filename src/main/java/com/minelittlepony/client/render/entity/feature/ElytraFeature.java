package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.PonyElytra;
import com.minelittlepony.client.render.IPonyRenderContext;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ElytraFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private static final Identifier TEXTURE_ELYTRA = new Identifier("textures/entity/elytra.png");

    @SuppressWarnings("unchecked")
    private final PonyElytra<T> modelElytra = (PonyElytra<T>)ModelType.ELYTRA.createModel();

    public ElytraFeature(IPonyRenderContext<T, M> rp) {
        super(rp);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ItemStack itemstack = entity.getEquippedStack(EquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            stack.push();
            preRenderCallback(stack);

            EntityModel<T> elytra = getElytraModel();

            getContextModel().copyStateTo(elytra);
            if (elytra instanceof PonyElytra) {
                ((PonyElytra<T>)elytra).isSneaking = PonyPosture.isCrouching(getContext().getEntityPony(entity), entity);
            }

            elytra.setAngles(entity, limbDistance, limbAngle, age, headYaw, headPitch);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(renderContext, modelElytra.getLayer(getElytraTexture(entity)), false, itemstack.hasGlint());
            modelElytra.render(stack, vertexConsumer, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

            stack.pop();
        }
    }

    protected void preRenderCallback(MatrixStack stack) {
        M body = getModelWrapper().body();
        stack.translate(0, body.getRiderYOffset(), 0.125);
        body.transform(BodyPart.BODY, stack);
    }

    protected EntityModel<T> getElytraModel() {
        return modelElytra;
    }

    protected Identifier getElytraTexture(T entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;

            Identifier result;

            if (player.hasSkinTexture()) {
                result = player.getElytraTexture();

                if (result != null) return result;
            }

            if (player.hasSkinTexture() && player.isPartVisible(PlayerModelPart.CAPE)) {
                result = player.getCapeTexture();

                if (result != null) return result;
            }
        }

        return TEXTURE_ELYTRA;
    }
}
