package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import com.minelittlepony.pony.IPony;

public final class DebugBoundingBoxRenderer {

    public static void render(IPony pony, LivingEntity entity, MatrixStack stack, VertexConsumerProvider renderContext) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!mc.getEntityRenderManager().shouldRenderHitboxes() || entity.squaredDistanceTo(mc.player) > 70) {
            return;
        }

        Box boundingBox = pony.getComputedBoundingBox(entity);

        Vec3d cam = mc.gameRenderer.getCamera().getPos();

        VertexConsumer vertices = renderContext.getBuffer(RenderLayer.getLines());

        WorldRenderer.method_22983(stack, vertices, VoxelShapes.cuboid(boundingBox), -cam.x, -cam.y, -cam.z, 1, 1, 1, 1);
    }
}
