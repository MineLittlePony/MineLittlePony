package com.minelittlepony.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class ModelPonyElytra extends ModelBase {
    private ModelRenderer rightWing;
    private ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

    public ModelPonyElytra() {
        this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
        this.rightWing = new ModelRenderer(this, 22, 0);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
    }

    @Override
    public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        this.leftWing.render(scale);
        this.rightWing.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        final float PI = (float) Math.PI;
        float rotateX = PI / 2F;
        float rotateZ = PI / 12;
        float rpY = PonyModelConstants.BODY_RP_Y_NOTSNEAK;
        float rotateY = PI / 8F;

        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isElytraFlying()) {
            float f4 = 1.0F;

            if (entityIn.motionY < 0.0D) {
                Vec3d vec3d = (new Vec3d(entityIn.motionX, entityIn.motionY, entityIn.motionZ)).normalize();
                f4 = 1.0F - (float) Math.pow(-vec3d.yCoord, 1.5D);
            }

            rotateX = f4 * PI * (2 / 3F) + (1.0F - f4) * rotateX;
            rotateY = f4 * ((float) Math.PI / 2F) + (1.0F - f4) * rotateY;
        } else if (entityIn.isSneaking()) {
            rotateX = ((float) Math.PI * 1.175F);
            rotateY = PI / 2;
            rpY = PonyModelConstants.BODY_RP_Y_SNEAK;
            rotateZ = PI / 4F;
        }

        this.leftWing.rotationPointX = 5.0F;
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
