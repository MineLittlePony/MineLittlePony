package com.minelittlepony.client.util.render;

import net.minecraft.client.util.math.MatrixStack;

public class MatrixStackUtil {
    static final MatrixStack ISOLATION_STACK = new MatrixStack();

    public static MatrixStack pushIsolation(MatrixStack matrices) {
        ISOLATION_STACK.peek().getNormalMatrix().set(matrices.peek().getNormalMatrix());
        ISOLATION_STACK.peek().getPositionMatrix().set(matrices.peek().getPositionMatrix());
        return ISOLATION_STACK;
    }

    public static void popIsolation() {
        while (!ISOLATION_STACK.isEmpty()) {
            ISOLATION_STACK.pop();
        }
    }
}
