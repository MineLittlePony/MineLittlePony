package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.*;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerModel extends RenderLiving {

    public RenderPlayerModel(RenderManager renderer) {
        super(renderer, new ModelPlayer(0, false), 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return ((EntityPlayerModel) var1).getSkinTexture();
    }

    @Override
    protected boolean canRenderName(EntityLivingBase targetEntity) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            return super.canRenderName(targetEntity);
        }
        return false;
    }

    @Override
    protected boolean setBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks, boolean p_177092_3_) {
        if (Minecraft.getMinecraft().theWorld != null) {
            return super.setBrightness(entitylivingbaseIn, partialTicks, p_177092_3_);
        }
        return false;
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        this.doRender((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
        popAttrib();
        pushMatrix();
        scale(1.0F, -1.0F, 1.0F);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        this.doRender((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
        popAttrib();
        popMatrix();
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2) {
        this.renderCloak((EntityPlayerModel) par1EntityLiving, par2);
    }

    protected void renderCloak(EntityPlayerModel entity, float par2) {
        super.preRenderCallback(entity, par2);
    }
}
