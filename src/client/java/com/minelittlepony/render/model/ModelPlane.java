package com.minelittlepony.render.model;

import net.minecraft.client.renderer.BufferBuilder;

import com.minelittlepony.util.render.Box;
import com.minelittlepony.util.render.Quad;
import com.minelittlepony.util.render.Vertex;

import javax.annotation.Nonnull;

public class ModelPlane extends Box<PlaneRenderer> {

    private Quad quad;

    public boolean hidden = false;

    public ModelPlane(PlaneRenderer renderer, int textureX, int textureY, float xMin, float yMin, float zMin, int w, int h, int d, float scale, Plane face) {
        super(renderer, textureX, textureY, xMin, yMin, zMin, w, h, d, scale, false);

        float xMax = xMin + w + scale;
        float yMax = yMin + h + scale;
        float zMax = zMin + d + scale;

        xMin -= scale;
        yMin -= scale;
        zMin -= scale;

        if (renderer.mirror) {
            float v = xMax;
            xMax = xMin;
            xMin = v;
        }

        if (renderer.mirrory) {
            float v = yMax;
            yMax = yMin;
            yMin = v;
        }

        if (renderer.mirrorz) {
            float v = zMax;
            zMax = zMin;
            zMin = v;
        }

        // w:west e:east d:down u:up s:south n:north
        Vertex wds = vert(xMin, yMin, zMin, 0, 0);
        Vertex eds = vert(xMax, yMin, zMin, 0, 8);
        Vertex eus = vert(xMax, yMax, zMin, 8, 8);
        Vertex wus = vert(xMin, yMax, zMin, 8, 0);
        Vertex wdn = vert(xMin, yMin, zMax, 0, 0);
        Vertex edn = vert(xMax, yMin, zMax, 0, 8);
        Vertex eun = vert(xMax, yMax, zMax, 8, 8);
        Vertex wun = vert(xMin, yMax, zMax, 8, 0);

        if (face == Plane.EAST) {
            quad = quad(textureX, d, textureY, h, edn, eds, eus, eun);
        }
        if (face == Plane.WEST) {
            quad = quad(textureX, d, textureY, h, wds, wdn, wun, wus);
        }
        if (face == Plane.UP) {
            quad = quad(textureX, w, textureY, d, edn, wdn, wds, eds);
        }
        if (face == Plane.DOWN) {
            quad = quad(textureX, w, textureY, d, eus, wus, wun, eun);
        }
        if (face == Plane.SOUTH) {
            quad = quad(textureX, w, textureY, h, eds, wds, wus, eus);
        }
        if (face == Plane.NORTH) {
            quad = quad(textureX, w, textureY, h, wdn, edn, eun, wun);
        }

        if (renderer.mirror || renderer.mirrory || renderer.mirrorz) {
            quad.flipFace();
        }
    }

    @Override
    public void render(@Nonnull BufferBuilder buffer, float scale) {
        if (!hidden) quad.draw(buffer, scale);
    }
}
