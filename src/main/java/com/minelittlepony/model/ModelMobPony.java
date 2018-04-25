package com.minelittlepony.model;

import com.minelittlepony.model.ponies.ModelPlayerPony;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMobPony extends ModelPlayerPony {

    public ModelMobPony() {
        super(false);
    }

    @Override
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        super.rotateLegs(move, swing, tick, entity);
        
        float var8 = MathHelper.sin(swingProgress * (float)Math.PI);
        float var9 = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * (float)Math.PI);
        
        rotateRightArm(var8, var9, move, tick);
        rotateLeftArm(var8, var9, move, tick);
    }
    
    protected void rotateRightArm(float var8, float var9, float move, float tick) {
        if (this.rightArmPose == ArmPose.EMPTY) return;
        
        if (!metadata.hasMagic()) {
            rotateArmHolding(bipedRightArm, 1, var8, var9, tick);
        } else {
            unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmRight, 1, var8, var9, tick);
        }
    }
    
    protected void rotateLeftArm(float var8, float var9, float move, float tick) {
        if (leftArmPose == ArmPose.EMPTY) return;

        if (!metadata.hasMagic()) {
            rotateArmHolding(bipedLeftArm, 1, var8, var9, tick);
        } else {
            unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmLeft, 1, var8, var9, tick);
        }
    }
}
