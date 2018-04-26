package com.minelittlepony.model.armour;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelZombiePonyArmor extends ModelPonyArmor {
    
    private boolean isRight(float move) {
        return MathHelper.sin(move / 20f) < 0;
    }
    
    // Copied from ModelZombiePony
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
        // Zombies are unidexterous.
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY) return;
        boolean right = isRight(move);
        float xchange = right ? 0.5f : -0.5f;
        ModelRenderer arm = right ? bipedRightArm : bipedLeftArm;
        
        shiftRotationPoint(arm, xchange, 1.5f, 3);
    }
}
