package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Model;

public class PonyRenderer extends AbstractRenderer<PonyRenderer> {

    public PonyRenderer(Model model) {
        super(model);
    }

    public PonyRenderer(Model model, int texX, int texY) {
        super(model, texX, texY);
    }

    @Override
    protected PonyRenderer copySelf() {
        return new PonyRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
