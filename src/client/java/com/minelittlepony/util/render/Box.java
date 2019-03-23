package com.minelittlepony.util.render;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public abstract class Box<T extends ModelRenderer> extends ModelBox {

    protected final T parent;

    public Box(T renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta) {
        super(renderer, texU, texV, x, y, z, dx, dy, dz, delta);
        parent = renderer;
    }

    public Box(T renderer, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
        super(renderer, texU, texV, x, y, z, dx, dy, dz, delta, mirror);
        parent = renderer;
    }

    /**
     * Creates a new vertex mapping the given (x, y, z) coordinates to a texture offset.
     */
    protected Vertex vert(float x, float y, float z, int texX, int texY) {
        return new Vertex(x, y, z, texX, texY);
    }

    /**
     * Creates a new quad with the given spacial vertices.
     */
    protected Quad quad(int startX, int width, int startY, int height, Vertex ...verts) {
        return new Quad(verts,
                startX,         startY,
                startX + width, startY + height,
                parent.textureWidth, parent.textureHeight);
    }
}