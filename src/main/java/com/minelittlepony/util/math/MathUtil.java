package com.minelittlepony.util.math;

import net.minecraft.util.math.MathHelper;

public class MathUtil {

    public static double clampLimit(double num, double limit) {
        return MathHelper.clamp(num, -limit, limit);
    }
}
