package com.minelittlepony.render.layer;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.components.PonyElytra;

import net.minecraft.client.entity.AbstractClientPlayer;
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
    private PonyElytra modelElytra = new PonyElytra();

    public LayerPonyElytra(RenderLivingBase<T> rp) {
        super(rp);
    }

    @Override
    public void doPonyRender(@Nonnull T entity, float move, float swing, float ticks, float age, float yaw, float head, float scale) {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color(1, 1, 1, 1);

            getRenderer().bindTexture(getElytraTexture(entity));

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.25F, 0.125F);
            getPlayerModel().transform(BodyPart.BODY);
            modelElytra.setRotationAngles(move, swing, age, yaw, head, scale, entity);
            modelElytra.render(entity, move, swing, age, yaw, head, scale);

            if (itemstack.isItemEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, modelElytra, move, swing, ticks, age, yaw, head, scale);
            }

            GlStateManager.popMatrix();
        }
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
