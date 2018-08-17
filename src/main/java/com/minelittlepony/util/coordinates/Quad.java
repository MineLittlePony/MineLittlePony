package com.minelittlepony.util.coordinates;

import net.minecraft.client.model.TexturedQuad;

public class Quad extends TexturedQuad {

    Quad(Vertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
        super(vertices, texcoordU1, texcoordV1, texcoordU2, texcoordV2, textureWidth, textureHeight);
    }
}