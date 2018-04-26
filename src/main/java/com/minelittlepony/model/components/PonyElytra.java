package com.minelittlepony.model.components;

import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class PonyElytra extends ModelBase {
    private PonyRenderer rightWing = new PonyRenderer(this, 22, 0);
    private PonyRenderer leftWing = new PonyRenderer(this, 22, 0);

    public PonyElytra() {
        this.leftWing          .box(-10, 0, 0, 10, 20, 2, 1);
        this.rightWing.mirror().box( 0,  0, 0, 10, 20, 2, 1);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        leftWing.render(scale);
        rightWing.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        final float PI = (float) Math.PI;
        float rotateX = PI / 2F;
        float rotateY = PI / 8F;
        float rotateZ = PI / 12;
        
        float rpY = PonyModelConstants.BODY_RP_Y_NOTSNEAK;
        
        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isElytraFlying()) {
            float f4 = 1;

            if (entityIn.motionY < 0) {
                Vec3d vec3d = (new Vec3d(entityIn.motionX, entityIn.motionY, entityIn.motionZ)).normalize();
                f4 = 1 - (float) Math.pow(-vec3d.y, 1.5);
            }

            rotateX = f4 * PI * (2 / 3F) + (1 - f4) * rotateX;
            rotateY = f4 * ((float) Math.PI / 2F) + (1 - f4) * rotateY;
        } else if (entityIn.isSneaking()) {
            rotateX = ((float) Math.PI * 1.175F);
            rotateY = PI / 2;
            rpY = PonyModelConstants.BODY_RP_Y_SNEAK;
            rotateZ = PI / 4F;
        }

        this.leftWing.rotationPointX = 5;
        this.leftWing.rotationPointY = rpY;

        if (entityIn instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) entityIn;
            abstractclientplayer.rotateElytraX = (float) (abstractclientplayer.rotateElytraX + (rotateX - abstractclientplayer.rotateElytraX) * 0.1D);
            abstractclientplayer.rotateElytraY = (float) (abstractclientplayer.rotateElytraY + (rotateY - abstractclientplayer.rotateElytraY) * 0.1D);
            abstractclientplayer.rotateElytraZ = (float) (abstractclientplayer.rotateElytraZ + (rotateZ - abstractclientplayer.rotateElytraZ) * 0.1D);
            this.leftWing.rotateAngleX = abstractclientplayer.rotateElytraX;
            this.leftWing.rotateAngleY = abstractclientplayer.rotateElytraY;
            this.leftWing.rotateAngleZ = abstractclientplayer.rotateElytraZ;
        } else {
            this.leftWing.rotateAngleX = rotateX;
            this.leftWing.rotateAngleZ = rotateZ;
            this.leftWing.rotateAngleY = rotateY;
        }

        this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
        this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
        this.rightWing.rotationPointY = this.leftWing.rotationPointY;
        this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
        this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
    }

}
