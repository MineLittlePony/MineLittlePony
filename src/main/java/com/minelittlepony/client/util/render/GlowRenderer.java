package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Model;

public class GlowRenderer extends AbstractRenderer<GlowRenderer> {
    public GlowRenderer(Model model, int x, int y) {
        super(model, x, y);
    }

    @Override
    public void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        boxes.add(new HornGlow(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
    }

    @Override
    protected GlowRenderer copySelf() {
        return new GlowRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
