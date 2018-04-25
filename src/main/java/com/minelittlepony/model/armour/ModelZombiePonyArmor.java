package com.minelittlepony.model.armour;

import net.minecraft.util.math.MathHelper;

public class ModelZombiePonyArmor extends ModelPonyArmor {
    
    private boolean isRight(float move) {
        return MathHelper.sin(move / 20f) < 0;
    }
    
    @Override
    protected void rotateRightArm(float var8, float var9, float move, float tick) {
        if (rightArmPose != ArmPose.EMPTY) return;
        
        if (isRight(move)) {
            rotateArmHolding(bipedRightArm, 1, var8, var9, tick);
        } else {
            rotateArmHolding(bipedLeftArm, -1, var8, var9, tick);
        }
    }
    
    @Override
    protected void rotateLeftArm(float var8, float var9, float move, float tick) {
        
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (this.rightArmPose != ArmPose.EMPTY) return;
        if (isRight(move)) {
            shiftRotationPoint(bipedRightArm, 0.5F, 1.5F, 3);
        } else {
            shiftRotationPoint(bipedLeftArm, -0.5F, 1.5F, 3);
        }
    }
}
