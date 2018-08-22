package com.minelittlepony.model.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.render.PonyRenderer;

public class Muffin implements IModelPart {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minelittlepony", "textures/models/muffin.png");

    private PonyRenderer crown;

    AbstractPonyModel model;

    public Muffin(AbstractPonyModel model) {
        this.model = model;
    }

    @Override
    public void init(float yOffset, float stretch) {
        crown = new PonyRenderer(model, 0, 0).size(64, 44)
                .around(-4, -12, -6)
                .box(0, 0, 0, 8, 4, 8, stretch)
                .box(3, -1.5F, 3, 2, 2, 2, stretch)
                .tex(0, 12).box(1.5F, -1, 1.5F, 5, 1, 5, stretch)
                .tex(0, 18).box(2, 1, 1, 4, 7, 6, stretch)
                .tex(0, 18).box(1, 1, 2, 6, 7, 4, stretch);
    }

    @Override
    public void renderPart(float scale) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        TextureManager tex = Minecraft.getMinecraft().getRenderManager().renderEngine;
        tex.bindTexture(TEXTURE);

        crown.render(scale);

        GlStateManager.popAttrib();
    }

}
