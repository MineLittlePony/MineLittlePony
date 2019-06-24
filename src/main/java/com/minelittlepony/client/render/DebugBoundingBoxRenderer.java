package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BoundingBox;

import com.minelittlepony.pony.IPony;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class DebugBoundingBoxRenderer {

    public static final DebugBoundingBoxRenderer instance = new DebugBoundingBoxRenderer();

    private DebugBoundingBoxRenderer() {
    }

    public void render(IPony pony, LivingEntity entity, float ticks) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!mc.getEntityRenderManager().shouldRenderHitboxes() || entity.squaredDistanceTo(mc.player) > 70) {
            return;
        }

        BoundingBox boundingBox = pony.getComputedBoundingBox(entity);

        enableBlend();
        blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        lineWidth(2);
        disableTexture();
        depthMask(false);

        WorldRenderer.drawBoxOutline(boundingBox.offset(mc.gameRenderer.getCamera().getPos().multiply(-1)), 1, 1, 0, 1);

        depthMask(true);
        enableTexture();
        disableBlend();
    }
}