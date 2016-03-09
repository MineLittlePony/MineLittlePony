package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.popAttrib;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.scale;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerModel<M extends EntityPlayerModel> extends RenderLiving<M> {

    public RenderPlayerModel(RenderManager renderer) {
        super(renderer, new ModelPlayer(0, false), 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(M var1) {
        return var1.getSkinTexture();
    }

    @Override
    protected boolean canRenderName(M targetEntity) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            return super.canRenderName(targetEntity);
        }
        return false;
    }

    @Override
    protected boolean setBrightness(M entitylivingbaseIn, float partialTicks, boolean p_177092_3_) {
        if (Minecraft.getMinecraft().theWorld != null) {
            return super.setBrightness(entitylivingbaseIn, partialTicks, p_177092_3_);
        }
        return false;
    }

    @Override
    public void doRender(M par1Entity, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        super.doRender(par1Entity, par2, par4, par6, par8, par9);
        popAttrib();
        pushMatrix();
        scale(1.0F, -1.0F, 1.0F);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        super.doRender(par1Entity, par2, par4, par6, par8, par9);
        popAttrib();
        popMatrix();
    }

    @Override
    protected void preRenderCallback(M par1EntityLiving, float par2) {
        this.renderCloak(par1EntityLiving, par2);
    }

    protected void renderCloak(M entity, float par2) {
        super.preRenderCallback(entity, par2);
    }
}
