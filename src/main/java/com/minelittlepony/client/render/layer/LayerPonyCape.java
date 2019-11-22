package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

import static com.minelittlepony.model.PonyModelConstants.PI;
import static com.mojang.blaze3d.platform.GlStateManager.*;

public class LayerPonyCape<M extends ClientPonyModel<AbstractClientPlayerEntity>> extends AbstractPonyLayer<AbstractClientPlayerEntity, M> {

    public LayerPonyCape(IPonyRender<AbstractClientPlayerEntity, M> context) {
        super(context);
    }

    @Override
    public void render(@Nonnull AbstractClientPlayerEntity player, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        M model = getModel();

        if (player.hasSkinTexture() && !player.isInvisible()
                && player.isSkinOverlayVisible(PlayerModelPart.CAPE) && player.getCapeTexture() != null
                && player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {

            pushMatrix();

            model.transform(BodyPart.BODY);
            translatef(0, 0.24F, 0);
            model.getBodyPart(BodyPart.BODY).applyTransform(scale);

            double capeX = MathHelper.lerp(partialTicks, player.field_7524, player.field_7500) - MathHelper.lerp(partialTicks, player.prevX, player.x);
            double capeY = MathHelper.lerp(partialTicks, player.field_7502, player.field_7521) - MathHelper.lerp(partialTicks, player.prevY, player.y);
            double capeZ = MathHelper.lerp(partialTicks, player.field_7522, player.field_7499) - MathHelper.lerp(partialTicks, player.prevZ, player.z);

            float motionYaw = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw) * scale;

            //double capeX = player.prevRenderX + (player.x - player.prevRenderX) * scale - (player.prevX + (player.x - player.prevX) * scale);
            //double capeY = player.prevRenderY + (player.y - player.prevRenderY) * scale - (player.prevY + (player.y - player.prevY) * scale);
            //double capeZ = player.prevRenderZ + (player.z - player.prevRenderZ) * scale - (player.prevZ + (player.z - player.prevZ) * scale);

            //float motionYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * scale;

            double sin = MathHelper.sin(motionYaw * PI / 180);
            double cos = (-MathHelper.cos(motionYaw * PI / 180));

            float capeMotionY = (float) capeY * 10;

            if (capeMotionY < -6) capeMotionY = -6;
            if (capeMotionY > 32) capeMotionY = 32;

            float capeMotionX = (float) (capeX * sin + capeZ * cos) * 100;

            float diagMotion =  (float) (capeX * cos - capeZ * sin) * 100;

            if (capeMotionX < 0) capeMotionX = 0;

            float camera = MathHelper.lerp(partialTicks, player.field_7505, player.field_7483);
            //float camera = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * scale;
            capeMotionY += MathHelper.sin(MathHelper.lerp(partialTicks, player.prevHorizontalSpeed, player.horizontalSpeed) * 6) * 32 * camera;

            rotatef(2 + capeMotionX / 12 + capeMotionY, 1, 0, 0);
            rotatef( diagMotion / 2, 0, 0, 1);
            rotatef(-diagMotion / 2, 0, 1, 0);
            rotatef(180, 0, 0, 1);
            rotatef(90, 1, 0, 0);
            getContext().bindTexture(player.getCapeTexture());
            model.renderCape(0.0625F);
            popMatrix();
        }
    }
}
