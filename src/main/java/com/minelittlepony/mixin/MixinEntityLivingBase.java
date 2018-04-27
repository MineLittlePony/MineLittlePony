package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.minelittlepony.ducks.IPonyAnimationHolder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IPonyAnimationHolder {
    @Shadow
    public float moveStrafing;

    private MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    // No other place to save this stuff? :'c
    // Add any animations you want
    // This could also go into Pony, but I'm unsure if that's a good place for it (@Immutable).
    private float strafeRollAmount = 0;

    @Override
    public float getStrafeAmount(float ticks) {
        float strafing = moveStrafing;
        if (strafing != 0) {
            if (Math.abs(strafeRollAmount) < Math.abs(strafing)) {
                strafeRollAmount += strafing/10;
            }
        } else {
            strafeRollAmount *= 0.8;
        }

        return (float)Math.toDegrees(strafeRollAmount);
    }
}
