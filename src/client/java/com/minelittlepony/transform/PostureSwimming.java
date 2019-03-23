package com.minelittlepony.transform;

import net.minecraft.entity.player.EntityPlayer;

import com.minelittlepony.util.math.MathUtil;

public class PostureSwimming extends PostureFlight {

    @Override
    protected double calculateRoll(EntityPlayer player, double motionX, double motionY, double motionZ) {
        motionX *= 2;
        motionZ *= 2;

        return super.calculateRoll(player, motionX, motionY, motionZ);
    }

    @Override
    protected double calculateIncline(EntityPlayer player, double motionX, double motionY, double motionZ) {
        double motionLerp = MathUtil.clampLimit(Math.sqrt(motionX * motionX + motionZ * motionZ) * 30, 1);

        return super.calculateIncline(player, motionX, motionY, motionZ) * motionLerp;
    }
}
