package com.minelittlepony.util.render;

import net.minecraft.client.model.TexturedQuad;

public class Quad extends TexturedQuad {

    Quad(Vertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        super(vertices, texcoordU1, texcoordV1, texcoordU2, texcoordV2, textureWidth, textureHeight);
    }

    /**
     * Reverses the order of the vertices belonging to this quad.
     * Positions of the vertices stay the same but the order of rendering is reversed to go counter-clockwise.
     *
     * Reversal also affects the cross-product used to calculate texture orientation.
     * <pre>
     * Normal:
     * 0-----1
     * |\    |
     * |  \  |
     * |    \|
     * 3-----2
     *
     * After flipFace:
     *
     * 3-----2
     * |    /|
     * |  /  |
     * |/    |
     * 0-----1
     * </pre>
     */
    @Override
    public void flipFace() {
       super.flipFace();
    }
}