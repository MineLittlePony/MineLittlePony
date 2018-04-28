package com.minelittlepony.util.coordinates;

import net.minecraft.client.model.PositionTextureVertex;

public class Vertex extends PositionTextureVertex {

    public Vertex(float x, float y, float z, int texX, int texY) {
        super(x, y, z, texX, texY);
    }

    public Vertex(Vertex old, float texX, float texY) {
        super(old, texX, texY);
    }

    public Vertex setTexturePosition(float texX, float texY) {
        texturePositionX = texX;
        texturePositionY = texY;
        return this;
    }

    /**
     * Creates a new vertex mapping the given (x, y, z) coordinates to a texture offset.
     */
    public static Vertex vert(float x, float y, float z, int texX, int texY) {
        return new Vertex(x, y, z, texX, texY);
    }
}
