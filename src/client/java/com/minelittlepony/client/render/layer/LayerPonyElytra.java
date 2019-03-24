package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.components.PonyElytra;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class LayerPonyElytra<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private final PonyElytra modelElytra = new PonyElytra();

    public LayerPonyElytra(RenderLivingBase<T> rp) {
        super(rp);
    }

    @Override
    public void render(@Nonnull T entity, float move, float swing, float partialTicks, float ticks, float yaw, float head, float scale) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color4f(1, 1, 1, 1);

            getRenderer().bindTexture(getElytraTexture(entity));

            GlStateManager.pushMatrix();
            preRenderCallback();

            ModelBase elytra = getElytraModel();

            if (elytra instanceof PonyElytra) {
                ((PonyElytra)elytra).isSneaking = getPonyRenderer().getEntityPony(entity).isCrouching(entity);
            }

            elytra.setRotationAngles(move, swing, ticks, yaw, head, scale, entity);
            elytra.render(entity, move, swing, ticks, yaw, head, scale);

            if (itemstack.isEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, elytra, move, swing, partialTicks, ticks, yaw, head, scale);
            }

            GlStateManager.popMatrix();
        }
    }

    protected void preRenderCallback() {
        GlStateManager.translatef(0, getPlayerModel().getRiderYOffset(), 0.125F);
        getPlayerModel().transform(BodyPart.BODY);
    }

    protected ModelBase getElytraModel() {
        return modelElytra;
    }

    protected ResourceLocation getElytraTexture(T entity) {
        if (entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity;

            ResourceLocation result;

            if (player.isPlayerInfoSet()) {
                result = player.getLocationElytra();

                if (result != null) return result;
            }

            if (player.hasPlayerInfo() && player.isWearing(EnumPlayerModelParts.CAPE)) {
                result = player.getLocationCape();

                if (result != null) return result;
            }
        }

        return TEXTURE_ELYTRA;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
