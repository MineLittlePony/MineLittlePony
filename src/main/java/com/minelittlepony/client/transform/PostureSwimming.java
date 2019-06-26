package com.minelittlepony.client.transform;

import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.util.math.MathUtil;
import com.mojang.blaze3d.platform.GlStateManager;

public class PostureSwimming extends PostureFlight {

    @Override
    protected double calculateRoll(PlayerEntity player, double motionX, double motionY, double motionZ) {
        motionX *= 2;
        motionZ *= 2;

        return super.calculateRoll(player, motionX, motionY, motionZ);
    }

    @Override
    protected double calculateIncline(PlayerEntity player, double motionX, double motionY, double motionZ) {
        double motionLerp = MathUtil.clampLimit(Math.sqrt(motionX * motionX + motionZ * motionZ) * 30, 1) / 2;

        GlStateManager.translated(0, 0.9, -1);

        return 90 + super.calculateIncline(player, motionX, motionY, motionZ) * motionLerp;
    }
}
