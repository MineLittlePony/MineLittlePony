package com.minelittlepony.model.ponies;

import com.minelittlepony.model.player.ModelAlicorn;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelIllagerPony extends ModelAlicorn {

    public ModelIllagerPony() {
        super(false);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        AbstractIllager illager = (AbstractIllager)entity;
        IllagerArmPose pose = illager.getArmPose();

        boolean rightHanded = illager.getPrimaryHand() == EnumHandSide.RIGHT;
        float mult = rightHanded ? 1 : -1;
        ModelRenderer arm = getArm(illager.getPrimaryHand());

        if (pose == IllagerArmPose.ATTACKING) {
            // vindicator attacking
            float f = MathHelper.sin(swingProgress * (float)Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float)Math.PI);


            float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

            bipedRightArm.rotateAngleZ = cos;
            bipedLeftArm.rotateAngleZ = cos;

            bipedRightArm.rotateAngleY = 0.15707964F;
            bipedLeftArm.rotateAngleY = -0.15707964F;

            arm.rotateAngleX = -1.8849558F + MathHelper.cos(ticks * 0.09F) * 0.15F;
            arm.rotateAngleX += f * 2.2F - f1 * 0.4F;

            bipedRightArm.rotateAngleX += sin;
            bipedLeftArm.rotateAngleX -= sin;
        } else if (pose == IllagerArmPose.SPELLCASTING) {
            // waving arms!
            arm.rotateAngleX = (float)(-0.75F * Math.PI);
            arm.rotateAngleZ = mult * MathHelper.cos(ticks * 0.6662F) / 4;
            arm.rotateAngleY = mult * 1.1F;
        } else if (pose == IllagerArmPose.BOW_AND_ARROW) {
            aimBow(arm, ticks);
        }
    }

    public ModelRenderer getArm(EnumHandSide side) {
        return canCast() ? getUnicornArmForSide(side) : getArmForSide(side);
    }
}
