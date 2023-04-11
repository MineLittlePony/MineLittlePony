package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.IPonyRenderContext;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.*;
import net.minecraft.entity.EquipmentSlot;

public class CapeFeature<M extends ClientPonyModel<AbstractClientPlayerEntity>> extends AbstractPonyFeature<AbstractClientPlayerEntity, M> {

    public CapeFeature(IPonyRenderContext<AbstractClientPlayerEntity, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        M model = getContextModel();

        if (player.hasSkinTexture() && !player.isInvisible()
                && player.isPartVisible(PlayerModelPart.CAPE) && player.getCapeTexture() != null
                && player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {

            stack.push();

            model.transform(BodyPart.BODY, stack);
            stack.translate(0, 0.24F, 0);
            model.getBodyPart(BodyPart.BODY).rotate(stack);

            double capeX = MathHelper.lerp(tickDelta, player.capeX, player.prevCapeX) - MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double capeY = MathHelper.lerp(tickDelta, player.capeY, player.prevCapeY) - MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double capeZ = MathHelper.lerp(tickDelta, player.capeZ, player.prevCapeZ) - MathHelper.lerp(tickDelta, player.prevZ, player.getZ());

            float motionYaw = player.prevBodyYaw + (player.bodyYaw - player.prevBodyYaw);

            double sin = MathHelper.sin(motionYaw * MathHelper.RADIANS_PER_DEGREE);
            double cos = -MathHelper.cos(motionYaw * MathHelper.RADIANS_PER_DEGREE);

            float capeMotionY = (float) capeY * 10;

            if (capeMotionY < -6) capeMotionY = -6;
            if (capeMotionY > 32) capeMotionY = 32;

            float capeMotionX = (float) (capeX * sin + capeZ * cos) * 100;

            float diagMotion =  (float) (capeX * cos - capeZ * sin) * 100;

            if (capeMotionX < 0) capeMotionX = 0;

            float camera = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
            capeMotionY += MathHelper.sin(MathHelper.lerp(tickDelta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6) * 32 * camera;

            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(2 + capeMotionX / 12 + capeMotionY));
            stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion( diagMotion / 2));
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-diagMotion / 2));
            stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));

            VertexConsumer vertices = renderContext.getBuffer(RenderLayer.getEntitySolid(player.getCapeTexture()));
            model.renderCape(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV);
            stack.pop();
        }
    }
}
