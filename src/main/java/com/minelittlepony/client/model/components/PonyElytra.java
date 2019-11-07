package com.minelittlepony.client.model.components;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.util.render.Part;
import com.mojang.blaze3d.platform.GlStateManager;

import static com.minelittlepony.model.PonyModelConstants.*;

/**
 * Modified from ModelElytra.
 */
public class PonyElytra<T extends LivingEntity> extends EntityModel<T> {

    public boolean isSneaking;

    private Part rightWing = new Part(this, 22, 0);
    private Part leftWing = new Part(this, 22, 0);

    public PonyElytra() {
        leftWing        .box(-10, 0, 0, 10, 20, 2, 1);
        rightWing.flip().box( 0,  0, 0, 10, 20, 2, 1);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyModel.render} for an explanation of the various parameters.
     */
    @Override
    public void render(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        leftWing.render(scale);
        rightWing.render(scale);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyModel.setRotationAngles} for an explanation of the various parameters.
     */
    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        float rotateX = PI / 2;
        float rotateY = PI / 8;
        float rotateZ = PI / 12;

        float rpY = BODY_RP_Y_NOTSNEAK;

        if (entity.isFallFlying()) {
            float velY = 1;

            Vec3d motion = entity.getVelocity();
            if (motion.y < 0) {
                velY = 1 - (float) Math.pow(-motion.normalize().y, 1.5);
            }

            rotateX = velY * PI * (2 / 3F) + (1 - velY) * rotateX;
            rotateY = velY * (PI / 2) + (1 - velY) * rotateY;
        } else if (isSneaking) {
            rotateX = PI * 1.175F;
            rotateY = PI / 2;
            rotateZ = PI / 4;
            rpY = BODY_RP_Y_SNEAK;
        }

        leftWing.rotationPointX = 5;
        leftWing.rotationPointY = rpY;

        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;

            player.elytraPitch += (rotateX - player.elytraPitch) / 10;
            player.elytraYaw += (rotateY - player.elytraYaw) / 10;
            player.elytraRoll += (rotateZ - player.elytraRoll) / 10;

            leftWing.pitch = player.elytraPitch;
            leftWing.yaw = player.elytraYaw;
            leftWing.roll = player.elytraRoll;
        } else {
            leftWing.pitch = rotateX;
            leftWing.yaw = rotateZ;
            leftWing.roll = rotateY;
        }

        rightWing.rotationPointX = -leftWing.rotationPointX;
        rightWing.rotationPointY = leftWing.rotationPointY;
        rightWing.pitch = leftWing.pitch;
        rightWing.yaw = -leftWing.yaw;
        rightWing.roll = -leftWing.roll;
    }

}
