package com.minelittlepony.render.transform;

import net.minecraft.client.entity.AbstractClientPlayer;

public class PostureSwimming extends PostureFlight {

    @Override
    protected double calculateRoll(AbstractClientPlayer player, double motionX, double motionY, double motionZ) {

        motionX *= 2;
        motionZ *= 2;

        return super.calculateRoll(player, motionX, motionY, motionZ);
    }

    @Override
    protected double calculateIncline(AbstractClientPlayer player, double motionX, double motionY, double motionZ) {
        return super.calculateIncline(player, motionX, motionY, motionZ);
    }
}
