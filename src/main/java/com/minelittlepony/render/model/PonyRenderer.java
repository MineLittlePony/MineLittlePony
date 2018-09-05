package com.minelittlepony.render.model;

import net.minecraft.client.model.ModelBase;

import com.minelittlepony.util.render.AbstractBoxRenderer;

public class PonyRenderer extends AbstractBoxRenderer<PonyRenderer> {

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
