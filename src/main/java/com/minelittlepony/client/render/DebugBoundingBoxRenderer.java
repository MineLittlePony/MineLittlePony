package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.RenderPass;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public final class DebugBoundingBoxRenderer {
    public static <T extends PonyRenderState> void render(T state, MatrixStack stack, VertexConsumerProvider matrices) {
        if (RenderPass.getCurrent() != RenderPass.WORLD) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (!client.getEntityRenderDispatcher().shouldRenderHitboxes() || state.invisible || client.hasReducedDebugInfo()) {
            return;
        }

        Vec3d offset = state.positionOffset;

        stack.push();
        stack.translate(-offset.x, -offset.y, -offset.z);

        double x = -state.x;
        double y = -state.y;
        double z = -state.z;

        VertexConsumer vertices = matrices.getBuffer(RenderLayer.getLines());

        VertexRendering.drawBox(stack, vertices, getBoundingBox(state).offset(x, y, z), 1, 1, 0, 1);
        stack.pop();
    }

    public static Box getBoundingBox(PonyRenderState state) {
        final float scale = state.getScaleFactor();
        final float width = state.width * scale;
        final float height = state.height * scale;

        return new Box(-width, 0, -width, width, height, width).offset(state.x, state.y, state.z);
    }
}
