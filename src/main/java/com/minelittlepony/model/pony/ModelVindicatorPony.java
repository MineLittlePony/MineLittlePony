package com.minelittlepony.model.pony;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelVindicatorPony extends ModelIllagerPony {

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        EntityVindicator vindicator = (EntityVindicator) entityIn;
        if (vindicator.isAggressive()) {
            float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
            this.rightForeLeg.rotateAngleZ = 0.0F;
            this.leftForeLeg.rotateAngleZ = 0.0F;

            if (((EntityLivingBase) entityIn).getPrimaryHand() == EnumHandSide.RIGHT) {
                this.rightForeLeg.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
//                this.leftForeLeg.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.rightForeLeg.rotateAngleX += f * 2.2F - f1 * 0.4F;
//                this.leftForeLeg.rotateAngleX += f * 1.2F - f1 * 0.4F;
            } else {
//                this.rightForeLeg.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.leftForeLeg.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
//                this.rightForeLeg.rotateAngleX += f * 1.2F - f1 * 0.4F;
                this.leftForeLeg.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            this.rightForeLeg.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.leftForeLeg.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.rightForeLeg.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftForeLeg.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

}
