package com.minelittlepony.model.ponies;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.model.armour.ModelZombiePonyArmor;
import com.minelittlepony.model.armour.PonyArmor;

import net.minecraft.util.math.MathHelper;

public class ModelZombiePony extends ModelMobPony {

    public ModelZombiePony() {
        super();
    }
    
    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelZombiePonyArmor(), new ModelZombiePonyArmor());
    }
    
    private boolean isRight(float move) {
        return MathHelper.sin(move / 20f) < 0;
    }
    
    @Override
    protected void rotateRightArm(float move, float tick) {
        if (rightArmPose != ArmPose.EMPTY) return;
        
        if (isRight(move)) {
            rotateArmHolding(bipedRightArm, 1, swingProgress, tick);
        } else {
            rotateArmHolding(bipedLeftArm, -1, swingProgress, tick);
        }
    }
    
    @Override
    protected void rotateLeftArm(float move, float tick) {
        
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY) return;
        
        if (isRight(move)) {
            shiftRotationPoint(bipedRightArm, 0.5F, 1.5F, 3);
        } else {
            shiftRotationPoint(bipedLeftArm, -0.5F, 1.5F, 3);
        }

    }
}
