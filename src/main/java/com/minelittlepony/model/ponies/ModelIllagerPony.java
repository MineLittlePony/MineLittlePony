package com.minelittlepony.model.ponies;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelIllagerPony extends ModelPlayerPony {

    public ModelIllagerPony() {
        super(false);
    }

    @Override
    public void setRotationAngles(float swing, float move, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

        super.setRotationAngles(swing, move, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        AbstractIllager illager = (AbstractIllager) entityIn;
        AbstractIllager.IllagerArmPose pose = illager.getArmPose();

        boolean rightHanded = illager.getPrimaryHand() == EnumHandSide.RIGHT;

        if (pose == AbstractIllager.IllagerArmPose.ATTACKING) {
            // vindicator attacking
            float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightArm.rotateAngleY = 0.15707964F;
            this.bipedLeftArm.rotateAngleY = -0.15707964F;

            if (rightHanded) {
                this.bipedRightArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.bipedRightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            } else {
                this.bipedLeftArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.bipedLeftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        } else if (pose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            if (this.metadata.hasMagic()) {
                this.horn.setUsingMagic(true);
            }
            // waving arms!
            if (rightHanded) {
//                this.bipedRightArm.rotationPointZ = 0.0F;
//                this.bipedRightArm.rotationPointX = -5.0F;
                this.bipedRightArm.rotateAngleX = (float) (-.75F * Math.PI);
                this.bipedRightArm.rotateAngleZ = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
                this.bipedRightArm.rotateAngleY = 1.1F;
            } else {
//                this.bipedLeftArm.rotationPointZ = 0.0F;
//                this.bipedLeftArm.rotationPointX = 5.0F;
                this.bipedLeftArm.rotateAngleX = (float) (-.75F * Math.PI);
                this.bipedLeftArm.rotateAngleZ = -MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
                this.bipedLeftArm.rotateAngleY = -1.1F;
            }

        } else if (pose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            if (rightHanded) {
                aimBow(ArmPose.EMPTY, ArmPose.BOW_AND_ARROW, ageInTicks);
            } else {
                aimBow(ArmPose.BOW_AND_ARROW, ArmPose.EMPTY, ageInTicks);
            }
        }
    }

    public ModelRenderer getArm(EnumHandSide side) {
        return metadata.hasMagic() ? side == EnumHandSide.LEFT ? this.unicornArmLeft : this.unicornArmRight : this.getArmForSide(side);
    }
}
