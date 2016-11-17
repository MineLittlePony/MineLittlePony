package com.minelittlepony.model;

import com.minelittlepony.renderer.PlaneRenderer;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.VertexBuffer;

public class ModelPlane extends ModelBox {

    private TexturedQuad[] quadList;
    private final Face face;

    public ModelPlane(PlaneRenderer renderer, int textureX, int textureY,
            float x, float y, float z, int w, int h, int d,
            float scale, Face face) {
        super(renderer, textureX, textureY, x, y, z, w, h, d, scale, false);
        this.face = face;

        this.quadList = new TexturedQuad[6];
        float x2 = x + w;
        float y2 = y + h;
        float z2 = z + d;
        x -= scale;
        y -= scale;
        z -= scale;
        x2 += scale;
        y2 += scale;
        z2 += scale;

        if (renderer.mirror) {
            float v = x2;
            x2 = x;
            x = v;
        }

        if (renderer.mirrory) {
            float v = y2;
            y2 = y;
            y = v;
        }

        if (renderer.mirrorz) {
            float v = z2;
            z2 = z;
            z = v;

        }

        // w:west e:east d:down u:up s:south n:north
        PositionTextureVertex wds = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex eds = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
        PositionTextureVertex eus = new PositionTextureVertex(x2, y2, z, 8.0F, 8.0F);
        PositionTextureVertex wus = new PositionTextureVertex(x, y2, z, 8.0F, 0.0F);
        PositionTextureVertex wdn = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
        PositionTextureVertex edn = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
        PositionTextureVertex eun = new PositionTextureVertex(x2, y2, z2, 8.0F, 8.0F);
        PositionTextureVertex wun = new PositionTextureVertex(x, y2, z2, 8.0F, 0.0F);

        // east
        this.quadList[0] = new TexturedQuad(
                new PositionTextureVertex[] { edn, eds, eus, eun },
                textureX, textureY,
                textureX + d, textureY + h,
                renderer.textureWidth, renderer.textureHeight);
        // west
        this.quadList[1] = new TexturedQuad(
                new PositionTextureVertex[] { wds, wdn, wun, wus },
                textureX, textureY,
                textureX + d, textureY + h,
                renderer.textureWidth, renderer.textureHeight);
        // down
        this.quadList[3] = new TexturedQuad(
                new PositionTextureVertex[] { edn, wdn, wds, eds },
                textureX, textureY,
                textureX + w, textureY + d,
                renderer.textureWidth, renderer.textureHeight);
        // up
        this.quadList[2] = new TexturedQuad(
                new PositionTextureVertex[] { eus, wus, wun, eun },
                textureX, textureY,
                textureX + w, textureY + d,
                renderer.textureWidth, renderer.textureHeight);
        // south
        this.quadList[4] = new TexturedQuad(
                new PositionTextureVertex[] { eds, wds, wus, eus },
                textureX, textureY,
                textureX + w, textureY + h,
                renderer.textureWidth, renderer.textureHeight);
        // north
        this.quadList[5] = new TexturedQuad(
                new PositionTextureVertex[] { wdn, edn, eun, wun },
                textureX, textureY,
                textureX + w, textureY + h,
                renderer.textureWidth, renderer.textureHeight);

        if (renderer.mirror || renderer.mirrory || renderer.mirrorz) {
            for (TexturedQuad texturedquad : this.quadList) {
                texturedquad.flipFace();
            }
        }
    }

    @Override
    public void render(VertexBuffer renderer, float scale) {
        this.quadList[this.face.ordinal()].draw(renderer, scale);
    }

    public static enum Face {
        EAST,
        WEST,
        DOWN,
        UP,
        SOUTH,
        NORTH;
    }
}
