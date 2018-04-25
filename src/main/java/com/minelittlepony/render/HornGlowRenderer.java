package com.minelittlepony.render;

import static net.minecraft.client.renderer.GlStateManager.color;

import com.minelittlepony.model.components.HornGlow;

import net.minecraft.client.model.ModelBase;

public class HornGlowRenderer extends BasePonyRenderer<HornGlowRenderer> {

    float r, g, b, a = 1;

    public HornGlowRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    public HornGlowRenderer setAlpha(float a) {
        this.a = a;

        return this;
    }

    public HornGlowRenderer setTint(int tint) {
        r = (tint >> 16 & 255) / 255.0F;
        g = (tint >> 8 & 255) / 255.0F;
        b = (tint & 255) / 255.0F;

        return this;
    }

    public void applyTint(float alpha) {
        color(r, g, b, alpha);
    }

    @Override
    public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        this.cubeList.add(new HornGlow(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, a));
    }

    @Override
    public void render(float scale) {
        super.render(scale);
        color(1, 1, 1, 1);
    }
}
