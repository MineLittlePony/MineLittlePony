package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.components.PonyElytra;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
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
        super(rp, new LayerElytra(rp));
    }

    @Override
    public void doPonyRender(@Nonnull T entity, float move, float swing, float ticks, float age, float yaw, float head, float scale) {

        AbstractPonyModel model = ((IRenderPony) getRenderer()).getPlayerModel().getModel();

        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color(1, 1, 1, 1);

            if (entity instanceof AbstractClientPlayer) {

                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                if (player.isPlayerInfoSet() && player.getLocationElytra() != null) {
                    getRenderer().bindTexture(player.getLocationElytra());
                } else if (player.hasPlayerInfo() && player.getLocationCape() != null && player.isWearing(EnumPlayerModelParts.CAPE)) {
                    getRenderer().bindTexture(player.getLocationCape());
                } else {
                    getRenderer().bindTexture(TEXTURE_ELYTRA);
                }
            } else {
                getRenderer().bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.25F, 0.125F);
            model.transform(BodyPart.BODY);
            modelElytra.setRotationAngles(move, swing, age, yaw, head, scale, entity);
            modelElytra.render(entity, move, swing, age, yaw, head, scale);

            if (itemstack.isItemEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, modelElytra, move, swing, ticks, age, yaw, head, scale);
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
