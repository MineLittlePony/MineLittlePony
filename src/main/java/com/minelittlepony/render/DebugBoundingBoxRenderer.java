package com.minelittlepony.render;

import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import com.minelittlepony.pony.data.IPony;

import static net.minecraft.client.renderer.GlStateManager.*;

public class DebugBoundingBoxRenderer {

    public static final DebugBoundingBoxRenderer instance = new DebugBoundingBoxRenderer();

    private DebugBoundingBoxRenderer() {
    }

    public void render(IPony pony, EntityLivingBase entity, float ticks) {
        AxisAlignedBB boundingBox = pony.getComputedBoundingBox(entity);

        EntityPlayer player = Minecraft.getMinecraft().player;
        double renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)ticks;
        double renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)ticks;
        double renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)ticks;

        enableBlend();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        glLineWidth(2.0F);
        disableTexture2D();
        depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(boundingBox.grow(0.002D).offset(-renderPosX, -renderPosY, -renderPosZ), 1, 1, 1, 1);

        depthMask(true);
        enableTexture2D();
        disableBlend();
    }
}
