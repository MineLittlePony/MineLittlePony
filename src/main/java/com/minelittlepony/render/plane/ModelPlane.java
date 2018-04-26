package com.minelittlepony.render.plane;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

import javax.annotation.Nonnull;

public class ModelPlane extends ModelBox {

    private TexturedQuad quad;
    
    public boolean hidden = false;
    
    public ModelPlane(PlaneRenderer renderer, int textureX, int textureY, float x, float y, float z, int w, int h, int d, float scale, Face face) {
        super(renderer, textureX, textureY, x, y, z, w, h, d, scale, false);

        float x2 = x + w + scale;
        float y2 = y + h + scale;
        float z2 = z + d + scale;
        
        x -= scale;
        y -= scale;
        z -= scale;
        
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
        PositionTextureVertex wds = new PositionTextureVertex(x , y , z , 0, 0);
        PositionTextureVertex eds = new PositionTextureVertex(x2, y , z , 0, 8);
        PositionTextureVertex eus = new PositionTextureVertex(x2, y2, z , 8, 8);
        PositionTextureVertex wus = new PositionTextureVertex(x , y2, z , 8, 0);
        PositionTextureVertex wdn = new PositionTextureVertex(x , y , z2, 0, 0);
        PositionTextureVertex edn = new PositionTextureVertex(x2, y , z2, 0, 8);
        PositionTextureVertex eun = new PositionTextureVertex(x2, y2, z2, 8, 8);
        PositionTextureVertex wun = new PositionTextureVertex(x , y2, z2, 8, 0);

        if (face == Face.EAST) { // North/Front (was East)
            quad = new TexturedQuad(new PositionTextureVertex[]{edn, eds, eus, eun}, textureX, textureY, textureX + d, textureY + h, renderer.textureWidth, renderer.textureHeight);
        }
        if (face == Face.WEST) { // South/Back (was West)
            quad = new TexturedQuad(new PositionTextureVertex[]{wds, wdn, wun, wus}, textureX, textureY, textureX + d, textureY + h, renderer.textureWidth, renderer.textureHeight);
        }
        if (face == Face.UP) { // Up
            quad = new TexturedQuad(new PositionTextureVertex[]{edn, wdn, wds, eds}, textureX, textureY, textureX + w, textureY + d, renderer.textureWidth, renderer.textureHeight);
        }
        if (face == Face.DOWN) { // Down
            quad = new TexturedQuad(new PositionTextureVertex[]{eus, wus, wun, eun}, textureX, textureY, textureX + w, textureY + d, renderer.textureWidth, renderer.textureHeight);
        }
        if (face == Face.SOUTH) { // East/Left (was South)
            quad = new TexturedQuad(new PositionTextureVertex[]{eds, wds, wus, eus}, textureX, textureY, textureX + w, textureY + h, renderer.textureWidth, renderer.textureHeight);
        }
        if (face == Face.NORTH) { // West/Right (was North)
            quad = new TexturedQuad(new PositionTextureVertex[]{wdn, edn, eun, wun}, textureX, textureY, textureX + w, textureY + h, renderer.textureWidth, renderer.textureHeight);
        }

        if (renderer.mirror || renderer.mirrory || renderer.mirrorz) {
            quad.flipFace();
        }
    }

    @Override
    public void render(@Nonnull BufferBuilder renderer, float scale) {
        if (!hidden) this.quad.draw(renderer, scale);
    }
}
