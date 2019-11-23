package com.minelittlepony.client.transform;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.model.IModel;

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
    public void transform(IModel model, PlayerEntity player, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        stack.translate(0, 0.9, -1);
        super.transform(model, player, stack, motionX, motionY, motionZ, yaw, ticks);
    }
}
