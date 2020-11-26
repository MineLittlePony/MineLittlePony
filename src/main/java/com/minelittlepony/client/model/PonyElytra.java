package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.ImmutableList;

import static com.minelittlepony.model.PonyModelConstants.*;

/**
 * Modified from ModelElytra.
 */
public class PonyElytra<T extends LivingEntity> extends AnimalModel<T> {

    public boolean isSneaking;

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public PonyElytra(ModelPart tree) {
        rightWing = tree.getChild("right_wing");
        leftWing = tree.getChild("left_wing");
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(leftWing, rightWing);
    }

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyModel.setRotationAngles} for an explanation of the various parameters.
     */
    @Override
    public void setAngles(T entity, float limbDistance, float limbAngle, float age, float headYaw, float headPitch) {
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

        leftWing.pivotX = 5;
        leftWing.pivotY = rpY;

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

        rightWing.pivotX = -leftWing.pivotX;
        rightWing.pivotY = leftWing.pivotY;
        rightWing.pitch = leftWing.pitch;
        rightWing.yaw = -leftWing.yaw;
        rightWing.roll = -leftWing.roll;
    }
}
