package com.minelittlepony.transform;

import net.minecraft.entity.player.EntityPlayer;

public class PostureSwimming extends PostureFlight {

    @Override
    protected double calculateRoll(EntityPlayer player, double motionX, double motionY, double motionZ) {

        motionX *= 2;
        motionZ *= 2;

        return super.calculateRoll(player, motionX, motionY, motionZ);
    }

    @Override
    protected double calculateIncline(EntityPlayer player, double motionX, double motionY, double motionZ) {
        return super.calculateIncline(player, motionX, motionY, motionZ);
    }
}
