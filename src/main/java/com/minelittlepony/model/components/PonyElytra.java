package com.minelittlepony.model.components;

import com.minelittlepony.render.PonyRenderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import static com.minelittlepony.model.PonyModelConstants.*;

/**
 * Modified from ModelElytra.
 */
public class PonyElytra extends ModelBase {
    private PonyRenderer rightWing = new PonyRenderer(this, 22, 0);
    private PonyRenderer leftWing = new PonyRenderer(this, 22, 0);

    public PonyElytra() {
        leftWing          .box(-10, 0, 0, 10, 20, 2, 1);
        rightWing.mirror().box( 0,  0, 0, 10, 20, 2, 1);
    }

    @Override
    public void render(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        leftWing.render(scale);
        rightWing.render(scale);
    }

    @Override
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);

        float rotateX = PI / 2;
        float rotateY = PI / 8;
        float rotateZ = PI / 12;

        float rpY = BODY_RP_Y_NOTSNEAK;

        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying()) {
            float velY = 1;

            if (entity.motionY < 0) {
                Vec3d motion = (new Vec3d(entity.motionX, entity.motionY, entity.motionZ)).normalize();
                velY = 1 - (float) Math.pow(-motion.y, 1.5);
            }

            rotateX = velY * PI * (2 / 3F) + (1 - velY) * rotateX;
            rotateY = velY * (PI / 2) + (1 - velY) * rotateY;
        } else if (entity.isSneaking()) {
            rotateX = PI * 1.175F;
            rotateY = PI / 2;
            rotateZ = PI / 4;
            rpY = BODY_RP_Y_SNEAK;
        }

        leftWing.rotationPointX = 5;
        leftWing.rotationPointY = rpY;

        if (entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity;

            player.rotateElytraX += (rotateX - player.rotateElytraX) / 10;
            player.rotateElytraY += (rotateY - player.rotateElytraY) / 10;
            player.rotateElytraZ += (rotateZ - player.rotateElytraZ) / 10;

            leftWing.rotateAngleX = player.rotateElytraX;
            leftWing.rotateAngleY = player.rotateElytraY;
            leftWing.rotateAngleZ = player.rotateElytraZ;
        } else {
            leftWing.rotateAngleX = rotateX;
            leftWing.rotateAngleZ = rotateZ;
            leftWing.rotateAngleY = rotateY;
        }

        rightWing.rotationPointX = -leftWing.rotationPointX;
        rightWing.rotationPointY = leftWing.rotationPointY;
        rightWing.rotateAngleX = leftWing.rotateAngleX;
        rightWing.rotateAngleY = -leftWing.rotateAngleY;
        rightWing.rotateAngleZ = -leftWing.rotateAngleZ;
    }

}
