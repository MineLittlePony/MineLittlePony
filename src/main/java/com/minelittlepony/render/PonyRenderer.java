package com.minelittlepony.render;

import net.minecraft.client.model.ModelBase;

public class PonyRenderer extends BasePonyRenderer<PonyRenderer> {

    public PonyRenderer(ModelBase model) {
        super(model);
    }

    public PonyRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }
    
    @Override
    protected PonyRenderer copySelf() {
        return new PonyRenderer(baseModel, textureOffsetX, textureOffsetY);
    }

}
