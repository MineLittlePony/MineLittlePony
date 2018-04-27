package com.minelittlepony.render;

import static net.minecraft.client.renderer.GlStateManager.color;

import net.minecraft.client.model.ModelBase;

public class HornGlowRenderer extends AbstractPonyRenderer<HornGlowRenderer> {

    float red;
    float green;
    float blue;
    float alpha = 1;

    public HornGlowRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    public HornGlowRenderer setAlpha(float alpha) {
        this.alpha = alpha;

        return this;
    }

    public HornGlowRenderer setTint(int tint) {
        red = (tint >> 16 & 255) / 255.0F;
        green = (tint >> 8 & 255) / 255.0F;
        blue = (tint & 255) / 255.0F;

        return this;
    }

    public void applyTint(float alpha) {
        color(red, green, blue, alpha);
    }

    @Override
    public void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        cubeList.add(new HornGlow(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, alpha));
    }

    @Override
    public void render(float scale) {
        super.render(scale);
        color(1, 1, 1, 1);
    }

    @Override
    protected HornGlowRenderer copySelf() {
        return new HornGlowRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
