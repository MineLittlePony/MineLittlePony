package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.PI;

public class LayerPonyCape<M extends ClientPonyModel<AbstractClientPlayerEntity>> extends AbstractPonyLayer<AbstractClientPlayerEntity, M> {

    public LayerPonyCape(IPonyRender<AbstractClientPlayerEntity, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        M model = getModel();

        if (player.hasSkinTexture() && !player.isInvisible()
                && player.isSkinOverlayVisible(PlayerModelPart.CAPE) && player.getCapeTexture() != null
                && player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {

            stack.push();

            model.transform(BodyPart.BODY, stack);
            stack.translate(0, 0.24F, 0);
            model.getBodyPart(BodyPart.BODY).rotate(stack);

            double capeX = MathHelper.lerp(tickDelta, player.field_7524, player.field_7500) - MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double capeY = MathHelper.lerp(tickDelta, player.field_7502, player.field_7521) - MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double capeZ = MathHelper.lerp(tickDelta, player.field_7522, player.field_7499) - MathHelper.lerp(tickDelta, player.prevZ, player.getZ());

            float motionYaw = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);

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

            float camera = MathHelper.lerp(tickDelta, player.field_7505, player.field_7483);
            //float camera = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * scale;
            capeMotionY += MathHelper.sin(MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6) * 32 * camera;

            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(2 + capeMotionX / 12 + capeMotionY));
            stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion( diagMotion / 2));
            stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-diagMotion / 2));
            stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));

            VertexConsumer vertices = renderContext.getBuffer(RenderLayer.getEntitySolid(player.getCapeTexture()));
            model.renderCape(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV);
            stack.pop();
        }
    }
}
