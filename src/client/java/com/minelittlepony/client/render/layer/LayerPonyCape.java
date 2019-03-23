package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

import static com.minelittlepony.model.PonyModelConstants.PI;
import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerPonyCape extends AbstractPonyLayer<AbstractClientPlayer> {

    public LayerPonyCape(RenderLivingBase<AbstractClientPlayer> entity) {
        super(entity);
    }

    @Override
    public void doRenderLayer(@Nonnull AbstractClientPlayer player, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        AbstractPonyModel model = getPlayerModel();

        if (player.hasPlayerInfo() && !player.isInvisible()
                && player.isWearing(EnumPlayerModelParts.CAPE) && player.getLocationCape() != null
                && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {

            pushMatrix();

            model.transform(BodyPart.BODY);
            translate(0, 0.24F, 0);
            model.getBody().postRender(scale);

            double capeX = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * scale - (player.prevPosX + (player.posX - player.prevPosX) * scale);
            double capeY = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * scale - (player.prevPosY + (player.posY - player.prevPosY) * scale);
            double capeZ = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * scale - (player.prevPosZ + (player.posZ - player.prevPosZ) * scale);

            float motionYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * scale;

            double sin = MathHelper.sin(motionYaw * PI / 180);
            double cos = (-MathHelper.cos(motionYaw * PI / 180));

            float capeMotionY = (float) capeY * 10;

            if (capeMotionY < -6) capeMotionY = -6;
            if (capeMotionY > 32) capeMotionY = 32;

            float capeMotionX = (float) (capeX * sin + capeZ * cos) * 100;

            float diagMotion =  (float) (capeX * cos - capeZ * sin) * 100;

            if (capeMotionX < 0) capeMotionX = 0;

            float camera = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * scale;
            capeMotionY += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * scale) * 6) * 32 * camera;

            rotate(2 + capeMotionX / 12 + capeMotionY, 1, 0, 0);
            rotate( diagMotion / 2, 0, 0, 1);
            rotate(-diagMotion / 2, 0, 1, 0);
            rotate(180, 0, 0, 1);
            rotate(90, 1, 0, 0);
            getRenderer().bindTexture(player.getLocationCape());
            model.renderCape(0.0625F);
            popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
