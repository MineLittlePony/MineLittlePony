package com.minelittlepony.client.util.render;

public class Vertex extends net.minecraft.client.model.Vertex {

    public Vertex(float x, float y, float z, float texX, float texY) {
        super(x, y, z, texX, texY);
    }

    private Vertex(Vertex old, float texX, float texY) {
        super(old, texX, texY);
    }

    // The MCP name is misleading.
    // This is meant to return a COPY with the given texture position
    @Override
    public Vertex remap(float texX, float texY) {
        return new Vertex(this, texX, texY);
    }
}
