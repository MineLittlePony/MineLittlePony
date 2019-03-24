package com.minelittlepony.client.render;

import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import com.minelittlepony.pony.IPony;

import static net.minecraft.client.renderer.GlStateManager.*;

public class DebugBoundingBoxRenderer {

    public static final DebugBoundingBoxRenderer instance = new DebugBoundingBoxRenderer();

    private DebugBoundingBoxRenderer() {
    }

    public void render(IPony pony, EntityLivingBase entity, float ticks) {
        Minecraft mc = Minecraft.getInstance();
        EntityPlayer player = mc.player;

        if (!mc.getRenderManager().isDebugBoundingBox() || entity.getDistanceSq(player) > 70) {
            return;
        }

        AxisAlignedBB boundingBox = pony.getComputedBoundingBox(entity);


        double renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)ticks;
        double renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)ticks;
        double renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)ticks;

        enableBlend();
        blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        lineWidth(2);
        disableTexture2D();
        depthMask(false);

        WorldRenderer.drawSelectionBoundingBox(boundingBox.grow(0.003D).offset(-renderPosX, -renderPosY, -renderPosZ), 1, 1, 0, 1);

        depthMask(true);
        enableTexture2D();
        disableBlend();
    }
}
