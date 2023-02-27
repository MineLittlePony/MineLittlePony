package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.common.util.animation.Interpolator;

public class ParaspriteModel<T extends LivingEntity> extends EntityModel<T> {

    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart jaw;
    private final ModelPart lips;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    private final ModelPart leftWing2;
    private final ModelPart rightWing2;

    public ParaspriteModel(ModelPart tree) {
        super(RenderLayer::getEntityTranslucent);
        child = false;
        root = tree;
        body = tree.getChild("body");
        jaw = body.getChild("jaw");
        lips = body.getChild("lips");
        leftWing = tree.getChild("leftWing");
        rightWing = tree.getChild("rightWing");
        leftWing2 = tree.getChild("leftWing2");
        rightWing2 = tree.getChild("rightWing2");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {

        root.pitch = MathHelper.clamp((float)entity.getVelocity().horizontalLength() / 10F, 0, 0.1F);
        body.pitch = 0;

        if (entity.hasPassengers()) {
            root.yaw = 0;
            root.pitch = 0;
        } else {
            root.yaw = headYaw * 0.017453292F;
            root.pitch = headPitch * 0.017453292F;
        }

        float sin = (float)Math.sin(ticks) / 2F;
        float cos = (float)Math.cos(ticks) / 3F;

        float jawOpenAmount = Interpolator.linear(entity.getUuid()).interpolate("jawOpen", entity instanceof VexEntity vex && vex.isCharging() ? 1 : 0, 10);

        jaw.pivotY = Math.max(0, 1.2F * jawOpenAmount);
        lips.pivotY = jaw.pivotY - 0.9F;
        lips.visible = jawOpenAmount > 0;
        body.pitch += 0.3F * jawOpenAmount;
        jaw.pitch = 0.4F * jawOpenAmount;
        lips.pitch = 0.2F * jawOpenAmount;

        float basWingExpand = 1;
        float innerWingExpand = basWingExpand / 2F;

        leftWing.pitch = 0;
        leftWing.roll = basWingExpand + cos + 0.3F;
        leftWing.yaw = basWingExpand - sin;

        rightWing.pitch = 0;
        rightWing.roll = -basWingExpand - cos - 0.3F;
        rightWing.yaw = -basWingExpand + sin;

        sin = -(float)Math.sin(ticks + Math.PI / 4F) / 2F;
        cos = (float)Math.cos(ticks + Math.PI / 4F) / 3F;

        leftWing2.pitch = 0;
        leftWing2.roll = innerWingExpand + sin - 0.3F;
        leftWing2.yaw = innerWingExpand - cos + 0.3F;

        rightWing2.pitch = 0;
        rightWing2.roll = -innerWingExpand - sin + 0.3F;
        rightWing2.yaw = -innerWingExpand + cos - 0.3F;
    }
}
