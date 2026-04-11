package com.minelittlepony.client.util.render;

import com.mojang.blaze3d.vertex.PoseStack;

public class MatrixStackUtil {
    static final PoseStack ISOLATION_STACK = new PoseStack();

    public static PoseStack pushIsolation(PoseStack matrices) {
        ISOLATION_STACK.last().normal().set(matrices.last().normal());
        ISOLATION_STACK.last().pose().set(matrices.last().pose());
        return ISOLATION_STACK;
    }

    public static void popIsolation() {
        while (!ISOLATION_STACK.isEmpty()) {
            ISOLATION_STACK.popPose();
        }
    }
}
