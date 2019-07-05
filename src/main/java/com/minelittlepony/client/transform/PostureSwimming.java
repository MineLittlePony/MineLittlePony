package com.minelittlepony.client.transform;

import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.model.IModel;
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
        return 90; // mojang handles this
    }

    @Override
    public void transform(IModel model, PlayerEntity player, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        GlStateManager.translated(0, 0.9, -1);
        super.transform(model, player, motionX, motionY, motionZ, yaw, ticks);
    }
}
