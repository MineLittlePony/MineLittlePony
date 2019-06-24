package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.components.PonyElytra;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

public class LayerPonyElytra<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private static final Identifier TEXTURE_ELYTRA = new Identifier("textures/entity/elytra.png");

    private final PonyElytra<T> modelElytra = new PonyElytra<>();

    public LayerPonyElytra(IPonyRender<T, M> rp) {
        super(rp);
    }

    @Override
    public void render(@Nonnull T entity, float move, float swing, float partialTicks, float ticks, float yaw, float head, float scale) {
        ItemStack itemstack = entity.getEquippedStack(EquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color4f(1, 1, 1, 1);

            getContext().bindTexture(getElytraTexture(entity));

            GlStateManager.pushMatrix();
            preRenderCallback();

            EntityModel<T> elytra = getElytraModel();

            if (elytra instanceof PonyElytra) {
                ((PonyElytra<T>)elytra).isSneaking = getContext().getEntityPony(entity).isCrouching(entity);
            }

            elytra.setAngles(entity, move, swing, ticks, yaw, head, scale);
            elytra.render(entity, move, swing, ticks, yaw, head, scale);

            if (itemstack.hasEnchantmentGlint()) {
                ArmorFeatureRenderer.renderEnchantedGlint(this::bindTexture, entity, elytra, move, swing, partialTicks, ticks, yaw, head, scale);
            }

            GlStateManager.popMatrix();
        }
    }

    protected void preRenderCallback() {
        GlStateManager.translatef(0, getPlayerModel().getRiderYOffset(), 0.125F);
        getPlayerModel().transform(BodyPart.BODY);
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

            if (player.hasSkinTexture() && player.isSkinOverlayVisible(PlayerModelPart.CAPE)) {
                result = player.getCapeTexture();

                if (result != null) return result;
            }
        }

        return TEXTURE_ELYTRA;
    }
}
