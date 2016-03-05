package com.brohoof.minelittlepony.renderer.layer;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.ducks.IRenderPony;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerPonyCape implements LayerRenderer<AbstractClientPlayer> {

    private RendererLivingEntity<? extends AbstractClientPlayer> renderer;
    private LayerCape cape;

    public LayerPonyCape(RendererLivingEntity<? extends AbstractClientPlayer> entity) {
        renderer = entity;
        this.cape = new LayerCape((RenderPlayer) entity);
    }

    public void doRenderLayer(AbstractClientPlayer clientPlayer, float p2, float p3, float ticks, float p5, float p6,
            float p7, float scale) {
        PlayerModel model = ((IRenderPony) renderer).getPony();
        if (model.getModel() instanceof ModelHumanPlayer) {
            cape.doRenderLayer(clientPlayer, p2, p3, ticks, p5, p6, p7, scale);
        } else if (clientPlayer.hasPlayerInfo() && !clientPlayer.isInvisible()
                && clientPlayer.isWearing(EnumPlayerModelParts.CAPE) && clientPlayer.getLocationCape() != null) {

            pushMatrix();
            translate(0.0F, 0.24F, 0.0F);
            if (model.getModel().metadata.getSize() == PonySize.FOAL) {
                translate(0.0F, 0.67F, -0.04F);
                scale(0.6F, 0.6F, 0.6F);
            } else if (model.getModel().metadata.getSize() == PonySize.LARGE) {
                translate(0.0F, -0.14F, -0.1F);
                scale(1.15F, 1.2F, 1.2F);
                if (model.getModel().isSneak && !model.getModel().isFlying) {
                    translate(0.0F, 0.03F, 0.0F);
                }
            } else if (model.getModel().metadata.getSize() == PonySize.TALL) {
                translate(0.0F, -0.09F, 0.0F);
                scale(1.0F, 1.0F, 1.0F);
                if (model.getModel().isSneak && !model.getModel().isFlying) {
                    translate(0.0F, 0.03F, 0.0F);
                }
            }

            if (model.getModel().isSneak && !model.getModel().isFlying) {
                translate(0.0F, 0.4F, -0.12F);
            }
            double d = clientPlayer.prevChasingPosX + (clientPlayer.chasingPosX - clientPlayer.prevChasingPosX) * scale
                    - (clientPlayer.prevPosX + (clientPlayer.posX - clientPlayer.prevPosX) * scale);
            double d1 = clientPlayer.prevChasingPosY + (clientPlayer.chasingPosY - clientPlayer.prevChasingPosY) * scale
                    - (clientPlayer.prevPosY + (clientPlayer.posY - clientPlayer.prevPosY) * scale);
            double d2 = clientPlayer.prevChasingPosZ + (clientPlayer.chasingPosZ - clientPlayer.prevChasingPosZ) * scale
                    - (clientPlayer.prevPosZ + (clientPlayer.posZ - clientPlayer.prevPosZ) * scale);
            float f10 = clientPlayer.prevRenderYawOffset
                    + (clientPlayer.renderYawOffset - clientPlayer.prevRenderYawOffset) * scale;
            double d3 = MathHelper.sin(f10 * 3.1415927F / 180.0F);
            double d4 = (-MathHelper.cos(f10 * 3.1415927F / 180.0F));
            float f12 = (float) d1 * 10.0F;
            if (f12 < -6.0F) {
                f12 = -6.0F;
            }

            if (f12 > 32.0F) {
                f12 = 32.0F;
            }

            float f13 = (float) (d * d3 + d2 * d4) * 100.0F;
            float f14 = (float) (d * d4 - d2 * d3) * 100.0F;
            if (f13 < 0.0F) {
                f13 = 0.0F;
            }

            float f15 = clientPlayer.prevCameraYaw + (clientPlayer.cameraYaw - clientPlayer.prevCameraYaw) * scale;
            f12 += MathHelper.sin((clientPlayer.prevDistanceWalkedModified
                    + (clientPlayer.distanceWalkedModified - clientPlayer.prevDistanceWalkedModified) * scale) * 6.0F)
                    * 32.0F * f15;
            if (clientPlayer.isSneaking()) {
                f12 += 25.0F;
            }

            rotate(2.0F + f13 / 12.0F + f12, 1.0F, 0.0F, 0.0F);
            rotate(f14 / 2.0F, 0.0F, 0.0F, 1.0F);
            rotate(-f14 / 2.0F, 0.0F, 1.0F, 0.0F);
            rotate(180.0F, 0.0F, 0.0F, 1.0F);
            rotate(90.0F, 1.0F, 0.0F, 0.0F);
            this.renderer.bindTexture(clientPlayer.getLocationCape());
            model.getModel().renderCape(0.0625F);
            popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
