package com.minelittlepony.util.math;

import net.minecraft.util.math.MathHelper;

public class MathUtil {

    public static double clampLimit(double num, double limit) {
        return MathHelper.clamp(num, -limit, limit);
    }

    public static float sensibleAngle(float angle) {
        angle %= 360;

        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;

        return angle;
    }
}
