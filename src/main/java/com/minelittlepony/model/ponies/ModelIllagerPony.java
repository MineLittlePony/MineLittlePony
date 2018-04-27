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
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {

        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);
        AbstractIllager illager = (AbstractIllager) entity;
        AbstractIllager.IllagerArmPose pose = illager.getArmPose();

        boolean rightHanded = illager.getPrimaryHand() == EnumHandSide.RIGHT;

        if (pose == AbstractIllager.IllagerArmPose.ATTACKING) {
            // vindicator attacking
            float f = MathHelper.sin(swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
            bipedRightArm.rotateAngleZ = 0.0F;
            bipedLeftArm.rotateAngleZ = 0.0F;
            bipedRightArm.rotateAngleY = 0.15707964F;
            bipedLeftArm.rotateAngleY = -0.15707964F;

            if (rightHanded) {
                bipedRightArm.rotateAngleX = -1.8849558F + MathHelper.cos(age * 0.09F) * 0.15F;
                bipedRightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            } else {
                bipedLeftArm.rotateAngleX = -1.8849558F + MathHelper.cos(age * 0.09F) * 0.15F;
                bipedLeftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            bipedRightArm.rotateAngleZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(age * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(age * 0.067F) * 0.05F;
        } else if (pose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            if (metadata.hasMagic()) {
                horn.setUsingMagic(true);
            }
            // waving arms!
            if (rightHanded) {
//                this.bipedRightArm.rotationPointZ = 0.0F;
//                this.bipedRightArm.rotationPointX = -5.0F;
                bipedRightArm.rotateAngleX = (float) (-.75F * Math.PI);
                bipedRightArm.rotateAngleZ = MathHelper.cos(age * 0.6662F) / 4;
                bipedRightArm.rotateAngleY = 1.1F;
            } else {
//                this.bipedLeftArm.rotationPointZ = 0.0F;
//                this.bipedLeftArm.rotationPointX = 5.0F;
                bipedLeftArm.rotateAngleX = (float) (-.75F * Math.PI);
                bipedLeftArm.rotateAngleZ = -MathHelper.cos(age * 0.6662F) / 4;
                bipedLeftArm.rotateAngleY = -1.1F;
            }

        } else if (pose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            if (rightHanded) {
                aimBow(ArmPose.EMPTY, ArmPose.BOW_AND_ARROW, age);
            } else {
                aimBow(ArmPose.BOW_AND_ARROW, ArmPose.EMPTY, age);
            }
        }
    }

    public ModelRenderer getArm(EnumHandSide side) {
        return metadata.hasMagic() ? side == EnumHandSide.LEFT ? unicornArmLeft : unicornArmRight : getArmForSide(side);
    }
}
