package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VexEntity;

import com.minelittlepony.common.util.animation.Interpolator;

public class ParaspriteModel<T extends LivingEntity> extends EntityModel<T> {

    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart jaw;
    private final ModelPart lips;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public ParaspriteModel(ModelPart tree) {
        super(RenderLayer::getEntityTranslucent);
        child = false;
        root = tree;
        body = tree.getChild("body");
        jaw = body.getChild("jaw");
        lips = body.getChild("lips");
        leftWing = tree.getChild("leftWing");
        rightWing = tree.getChild("rightWing");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        body.pitch = 0;
        lips.visible = false;

        if (entity.hasPassengers()) {
            root.yaw = 0;
            root.pitch = 0;
        } else {
            root.yaw = headYaw * 0.017453292F;
            root.pitch = headPitch * 0.017453292F;
        }

        float sin = (float)Math.sin(ticks) / 2;
        float cos = (float)Math.cos(ticks) / 3;

        float jawOpenAmount = Interpolator.linear(entity.getUuid()).interpolate("jawOpen", entity instanceof VexEntity vex && vex.isCharging() ? 1 : 0, 10);

        if (jawOpenAmount > 0) {
            jaw.pivotY = Math.max(0, 2 * jawOpenAmount);
            lips.pivotY = jaw.pivotY - 1;
            lips.visible = true;
            body.pitch += 0.3F * jawOpenAmount;
            jaw.pitch = 0.6F * jawOpenAmount;
            lips.pitch = 0.25F * jawOpenAmount;
        }

        leftWing.visible = true;
        leftWing.roll = 0.5F + cos;
        leftWing.yaw = 0.5F - sin;

        rightWing.visible = true;
        rightWing.roll = -0.5F - cos;
        rightWing.yaw = -0.5F + sin;
    }
}
