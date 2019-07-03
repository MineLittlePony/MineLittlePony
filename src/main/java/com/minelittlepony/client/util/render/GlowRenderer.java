package com.minelittlepony.client.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;

public class GlowRenderer extends AbstractRenderer<GlowRenderer> {

    int tint;
    float alpha = 1;

    public GlowRenderer(Model model, int x, int y) {
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

    @Override
    public void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        boxes.add(new HornGlow(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, alpha));
    }

    @Override
    public void render(float scale) {
        MinecraftClient.getInstance().gameRenderer.disableLightmap();
        Color.glColor(tint, alpha);
        super.render(scale);
        MinecraftClient.getInstance().gameRenderer.enableLightmap();
    }

    @Override
    protected GlowRenderer copySelf() {
        return new GlowRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
