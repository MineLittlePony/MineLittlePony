package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;

public class ParaspriteModel extends EntityModel<StriderEntity> {

    private ModelPart body;
    private ModelPart leftWing;
    private ModelPart rightWing;

    private ModelPart saddle;

    public ParaspriteModel(ModelPart tree) {
        super(RenderLayer::getEntityTranslucent);
        child = false;
        body = tree.getChild("body");
        saddle = tree.getChild("saddle");
        leftWing = tree.getChild("leftWing");
        rightWing = tree.getChild("rightWing");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        saddle.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(StriderEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {

        if (entity.hasPassengers()) {
            body.yaw = 0;
            body.pitch = 0;
        } else {
            body.yaw = headYaw * 0.017453292F;
            body.pitch = headPitch * 0.017453292F;
        }
        saddle.copyTransform(body);

        float sin = (float)Math.sin(ticks) / 2;
        float cos = (float)Math.cos(ticks) / 3;

        leftWing.roll = 0.5F + cos;
        leftWing.yaw = 0.5F - sin;

        rightWing.visible = true;
        rightWing.roll = -0.5F - cos;
        rightWing.yaw = -0.5F + sin;
    }
}
