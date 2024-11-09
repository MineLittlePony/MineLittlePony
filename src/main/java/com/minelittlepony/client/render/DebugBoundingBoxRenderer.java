package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.RenderPass;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.PonyBounds;
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

        Box boundingBox = PonyBounds.getBoundingBox(state.pony, state);

        double x = -state.x;
        double y = -state.y;
        double z = -state.z;

        VertexConsumer vertices = matrices.getBuffer(RenderLayer.getLines());

        WorldRenderer.drawBox(stack, vertices, boundingBox.offset(x, y, z), 1, 1, 0, 1);
        stack.pop();
    }
}
