package com.minelittlepony.util;

import net.minecraft.util.math.MathHelper;

public interface MathUtil {
    interface Angles {
        float
            _270_DEG = 270 * MathHelper.RADIANS_PER_DEGREE,
            _90_DEG = 90 * MathHelper.RADIANS_PER_DEGREE,
            _30_DEG = 30 * MathHelper.RADIANS_PER_DEGREE
        ;
    }

    static double clampLimit(double num, double limit) {
        return MathHelper.clamp(num, -limit, limit);
    }

    static int mod(int value, int mod) {
        value %= mod;

        while (value < 0) value += mod;

        return value;
    }

    static float interpolateDegress(float prev, float current, float partialTicks) {
        float difference = current - prev;

        while (difference < -180) difference += 360;
        while (difference >= 180) difference -= 360;

        return prev + partialTicks * difference;
    }

    static boolean compareFloats(float a, float b) {
        return Math.abs(a - b) <= 0.001F;
    }
}
