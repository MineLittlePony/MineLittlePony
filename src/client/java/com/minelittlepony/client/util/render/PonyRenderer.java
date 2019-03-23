package com.minelittlepony.client.util.render;

import net.minecraft.client.model.ModelBase;

public class PonyRenderer extends AbstractRenderer<PonyRenderer> {

    public PonyRenderer(ModelBase model) {
        super(model);
    }

    public PonyRenderer(ModelBase model, int texX, int texY) {
        super(model, texX, texY);
    }

    @Override
    protected PonyRenderer copySelf() {
        return new PonyRenderer(baseModel, textureOffsetX, textureOffsetY);
    }
}
