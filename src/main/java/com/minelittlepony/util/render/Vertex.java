package com.minelittlepony.util.render;

import net.minecraft.client.model.PositionTextureVertex;

public class Vertex extends PositionTextureVertex {

    Vertex(float x, float y, float z, int texX, int texY) {
        super(x, y, z, texX, texY);
    }

    private Vertex(Vertex old, float texX, float texY) {
        super(old, texX, texY);
    }

    // The MCP name is misleading.
    // This is meant to return a COPY with the given texture position
    @Override
    public Vertex setTexturePosition(float texX, float texY) {
        return new Vertex(this, texX, texY);
    }
}
