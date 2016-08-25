package com.voxelmodpack.hdskins.gui;

import static net.minecraft.client.renderer.GlStateManager.*;

import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerModel<M extends EntityPlayerModel> extends RenderLivingBase<M> {

    private static final ModelPlayer FAT = new ModelPlayer(0, false);
    private static final ModelPlayer THIN = new ModelPlayer(0, true);

    public RenderPlayerModel(RenderManager renderer) {
        super(renderer, FAT, 0.0F);
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

    public ModelPlayer getEntityModel(M entity) {
        if (entity.metaHandler != null && entity.metaHandler.get("slim").isPresent()) {
            boolean skinny = "true".equals(entity.metaHandler.get("slim").get());
            return skinny ? THIN : FAT;
        }
        return FAT;

    }

    @Override
    public void doRender(M par1Entity, double par2, double par4, double par6, float par8, float par9) {
        ModelPlayer player = this.getEntityModel(par1Entity);
        this.mainModel = player;

        Set<EnumPlayerModelParts> parts = Minecraft.getMinecraft().gameSettings.getModelParts();
        player.bipedHeadwear.isHidden = !parts.contains(EnumPlayerModelParts.HAT);
        player.bipedBodyWear.isHidden = !parts.contains(EnumPlayerModelParts.JACKET);
        player.bipedLeftLegwear.isHidden = !parts.contains(EnumPlayerModelParts.LEFT_PANTS_LEG);
        player.bipedRightLegwear.isHidden = !parts.contains(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        player.bipedLeftArmwear.isHidden = !parts.contains(EnumPlayerModelParts.LEFT_SLEEVE);
        player.bipedRightArmwear.isHidden = !parts.contains(EnumPlayerModelParts.RIGHT_SLEEVE);

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
