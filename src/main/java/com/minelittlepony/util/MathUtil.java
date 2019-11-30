package com.minelittlepony.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil {

    public static double clampLimit(double num, double limit) {
        return MathHelper.clamp(num, -limit, limit);
    }

    public static int mod(int value, int mod) {
        value %= mod;

        while (value < 0) value += mod;

        return value;
    }

    public static float sensibleAngle(float angle) {
        angle %= 360;

        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;

        return angle;
    }

    public static float interpolateDegress(float prev, float current, float partialTicks) {
        float difference = current - prev;

        while (difference < -180) difference += 360;
        while (difference >= 180) difference -= 360;

        return prev + partialTicks * difference;
    }

    public static float interpolateRadians(float prev, float current, float partialTicks) {
        return (float)Math.toRadians(interpolateDegress(prev, current, partialTicks));
    }

    public static boolean compareFloats(float a, float b) {
        return Math.abs(a - b) <= 0.001F;
    }
}
