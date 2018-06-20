package com.minelittlepony.render;

import com.minelittlepony.model.components.ModelPonyHead;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.render.PonySkullRenderer.ISkull;

public abstract class PonySkull implements ISkull {

    private static ModelPonyHead ponyHead = new ModelPonyHead();

    @Override
    public void preRender(boolean transparency) {

    }

    @Override
    public void bindPony(Pony pony) {
        ponyHead.metadata = pony.getMetadata();
    }

    @Override
    public void render(float animateTicks, float rotation, float scale) {
        ponyHead.render(null, animateTicks, 0, 0, rotation, 0, scale);
    }
}