package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.minelittlepony.ducks.IPonyAnimationHolder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements IPonyAnimationHolder {
    private MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    // No other place to save this stuff? :'c
    // Add any animations you want
    // This could also go into Pony, but I'm unsure if that's a good place for it (@Immutable).
    private float strafeRollAmount = 0;

    @Override
    public float getStrafeAmount() {
        return strafeRollAmount;
    }

    @Override
    public void setStrafeAmount(float strafeAmount) {
        strafeRollAmount = strafeAmount;
    }
}
