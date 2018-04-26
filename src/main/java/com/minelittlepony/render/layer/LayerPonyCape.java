package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelWrapper;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

import static net.minecraft.client.renderer.GlStateManager.*;
import static com.minelittlepony.model.PonyModelConstants.PI;

public class LayerPonyCape extends AbstractPonyLayer<AbstractClientPlayer> {

    public LayerPonyCape(RenderLivingBase<? extends AbstractClientPlayer> entity) {
        super(entity, new LayerCape((RenderPlayer) entity));
    }

    @Override
    public void doPonyRender(@Nonnull AbstractClientPlayer clientPlayer, float p2, float p3, float ticks, float p5, float p6, float p7, float scale) {
        ModelWrapper model = ((IRenderPony) getRenderer()).getPlayerModel();
        if (clientPlayer.hasPlayerInfo() && !clientPlayer.isInvisible()
                && clientPlayer.isWearing(EnumPlayerModelParts.CAPE) && clientPlayer.getLocationCape() != null
                && clientPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            pushMatrix();
            model.getModel().transform(BodyPart.BODY);
            translate(0, 0.24F, 0);
            model.getModel().bipedBody.postRender(scale);

            double d = clientPlayer.prevChasingPosX + (clientPlayer.chasingPosX - clientPlayer.prevChasingPosX) * scale - (clientPlayer.prevPosX + (clientPlayer.posX - clientPlayer.prevPosX) * scale);
            double d1 = clientPlayer.prevChasingPosY + (clientPlayer.chasingPosY - clientPlayer.prevChasingPosY) * scale - (clientPlayer.prevPosY + (clientPlayer.posY - clientPlayer.prevPosY) * scale);
            double d2 = clientPlayer.prevChasingPosZ + (clientPlayer.chasingPosZ - clientPlayer.prevChasingPosZ) * scale - (clientPlayer.prevPosZ + (clientPlayer.posZ - clientPlayer.prevPosZ) * scale);
            float f10 = clientPlayer.prevRenderYawOffset + (clientPlayer.renderYawOffset - clientPlayer.prevRenderYawOffset) * scale;
            double d3 = MathHelper.sin(f10 * PI / 180);
            double d4 = (-MathHelper.cos(f10 * PI / 180));
            float f12 = (float) d1 * 10;
            if (f12 < -6.0F) {
                f12 = -6.0F;
            }

            if (f12 > 32) {
                f12 = 32;
            }

            float f13 = (float) (d * d3 + d2 * d4) * 100;
            float f14 = (float) (d * d4 - d2 * d3) * 100;
            if (f13 < 0) {
                f13 = 0;
            }

            float f15 = clientPlayer.prevCameraYaw + (clientPlayer.cameraYaw - clientPlayer.prevCameraYaw) * scale;
            f12 += MathHelper.sin((clientPlayer.prevDistanceWalkedModified + (clientPlayer.distanceWalkedModified - clientPlayer.prevDistanceWalkedModified) * scale) * 6) * 32 * f15;

            rotate(2 + f13 / 12 + f12, 1, 0, 0);
            rotate(f14 / 2, 0, 0, 1);
            rotate(-f14 / 2, 0, 1, 0);
            rotate(180, 0, 0, 1);
            rotate(90, 1, 0, 0);
            this.getRenderer().bindTexture(clientPlayer.getLocationCape());
            model.getModel().renderCape(0.0625F);
            popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
