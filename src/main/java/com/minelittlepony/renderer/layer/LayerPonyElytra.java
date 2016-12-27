package com.minelittlepony.renderer.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelPonyElytra;
import com.minelittlepony.model.pony.ModelHumanPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class LayerPonyElytra implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    private RenderLivingBase<?> renderPlayer;
    private ModelPonyElytra modelElytra = new ModelPonyElytra();

    private LayerElytra elytra;

    public LayerPonyElytra(RenderLivingBase<?> rp) {
        this.renderPlayer = rp;
        this.elytra = new LayerElytra(rp);
    }

    @Override
    public void doRenderLayer(@Nonnull EntityLivingBase entity, float swing, float swingAmount, float ticks, float age, float yaw, float head, float scale) {

        AbstractPonyModel model = ((IRenderPony) this.renderPlayer).getPony().getModel();
        if (model instanceof ModelHumanPlayer) {
            this.elytra.doRenderLayer(entity, swing, swingAmount, ticks, age, yaw, head, scale);
            return;
        }

        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack.getItem() == Items.ELYTRA) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (entity instanceof AbstractClientPlayer) {

                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                if (player.isPlayerInfoSet() && player.getLocationElytra() != null) {
                    this.renderPlayer.bindTexture(player.getLocationElytra());
                } else if (player.hasPlayerInfo() && player.getLocationCape() != null && player.isWearing(EnumPlayerModelParts.CAPE)) {
                    this.renderPlayer.bindTexture(player.getLocationCape());
                } else {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
            } else {
                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.25F, 0.125F);
            model.transform(BodyPart.BODY);
            this.modelElytra.setRotationAngles(swing, swingAmount, age, yaw, head, scale, entity);
            this.modelElytra.render(entity, swing, swingAmount, age, yaw, head, scale);

            if (itemstack.isItemEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entity, this.modelElytra, swing, swingAmount, ticks, age, yaw, head, scale);
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
