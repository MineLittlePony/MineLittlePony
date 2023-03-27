package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.model.RenderPass;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.PonyBounds;

public final class DebugBoundingBoxRenderer {

    public static <T extends LivingEntity> void render(IPony pony, EntityRenderer<T> renderer, T entity, MatrixStack stack, VertexConsumerProvider renderContext, float tickDelta) {

        if (RenderPass.getCurrent() != RenderPass.WORLD) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (!client.getEntityRenderDispatcher().shouldRenderHitboxes() || entity.isInvisible() || client.hasReducedDebugInfo()) {
            return;
        }

        Vec3d offset = renderer.getPositionOffset(entity, tickDelta);

        stack.push();
        stack.translate(-offset.x, -offset.y, -offset.z);

        Box boundingBox = PonyBounds.getBoundingBox(pony, entity);

        Vec3d pos = entity.getPos();

        double x = - MathHelper.lerp(tickDelta, entity.lastRenderX, pos.x);
        double y = - MathHelper.lerp(tickDelta, entity.lastRenderY, pos.y);
        double z = - MathHelper.lerp(tickDelta, entity.lastRenderZ, pos.z);

        VertexConsumer vertices = renderContext.getBuffer(RenderLayer.getLines());

        WorldRenderer.drawBox(stack, vertices, boundingBox.offset(x, y, z), 1, 1, 0, 1);
        stack.pop();
    }
}
