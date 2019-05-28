package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;

import com.minelittlepony.pony.IPony;

import static com.mojang.blaze3d.platform.GlStateManager.*;

public class DebugBoundingBoxRenderer {

    public static final DebugBoundingBoxRenderer instance = new DebugBoundingBoxRenderer();

    private DebugBoundingBoxRenderer() {
    }

    public void render(IPony pony, LivingEntity entity, float ticks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (!mc.getEntityRenderManager().shouldRenderHitboxes() || entity.squaredDistanceTo(player) > 70) {
            return;
        }

        BoundingBox boundingBox = pony.getComputedBoundingBox(entity);


        double renderPosX = player.prevX + (player.x - player.prevX) * (double)ticks;
        double renderPosY = player.prevY + (player.y - player.prevY) * (double)ticks;
        double renderPosZ = player.prevZ + (player.z - player.prevZ) * (double)ticks;

        enableBlend();
        blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        lineWidth(2);
        disableTexture();
        depthMask(false);

        WorldRenderer.drawBoxOutline(boundingBox.expand(0.003D).offset(-renderPosX, -renderPosY, -renderPosZ), 1, 1, 0, 1);

        depthMask(true);
        enableTexture();
        disableBlend();
    }
}
