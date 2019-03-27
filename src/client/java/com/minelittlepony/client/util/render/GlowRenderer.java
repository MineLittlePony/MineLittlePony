package com.minelittlepony.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelBase;

import org.lwjgl.opengl.GL11;

public class GlowRenderer extends AbstractRenderer<GlowRenderer> {

    int tint;
    float alpha = 1;

    public GlowRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    public GlowRenderer setAlpha(float alpha) {
        this.alpha = alpha;

        return this;
    }

    public GlowRenderer setTint(int tint) {
        this.tint = tint;

        return this;
    }

    public void applyTint(float alpha) {
        Color.glColor(tint, alpha);
    }

    @Override
    public void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        cubeList.add(new HornGlow(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, alpha));
    }

    @Override
    public void render(float scale) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        Minecraft.getInstance().gameRenderer.disableLightmap();
        super.render(scale);
        GL11.glPopAttrib();
    }

    @Override
    protected GlowRenderer copySelf() {
        return new GlowRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
