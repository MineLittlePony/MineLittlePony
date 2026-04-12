package com.minelittlepony.util;

import net.minecraft.util.Mth;

public interface MathUtil {
    float QUARTER_PIE = Mth.PI * 0.25F;

    interface Angles {
        float
            _270_DEG = 270 * Mth.DEG_TO_RAD,
            _90_DEG = 90 * Mth.DEG_TO_RAD,
            _30_DEG = 30 * Mth.DEG_TO_RAD
        ;
    }

    static double clampLimit(double num, double limit) {
        return Mth.clamp(num, -limit, limit);
    }

    static int mod(int value, int mod) {
        value %= mod;

        while (value < 0) value += mod;

        return value;
    }

    static boolean compareFloats(float a, float b) {
        return Math.abs(a - b) <= 0.001F;
    }
}
