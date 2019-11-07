package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Box;
import net.minecraft.client.model.Quad;
import net.minecraft.client.model.Vertex;
import net.minecraft.client.render.BufferBuilder;

class Cone extends Box {

    private Quad[] polygons;

    public Cone(Part renderer, int texX, int texY, float xMin, float yMin, float zMin, int w, int h, int d, float scale) {
        super(renderer, texX, texY, xMin, yMin, zMin, w, h, d, scale);

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

        float tipInset = 0.4f;

        float tipXmin = xMin + w * tipInset;
        float tipZmin = zMin + d * tipInset;
        float tipXMax = xMax - w * tipInset;
        float tipZMax = zMax - d * tipInset;

        // w:west e:east d:down u:up s:south n:north
        Vertex wds = renderer.vert(tipXmin, yMin, tipZmin, 0, 0);
        Vertex eds = renderer.vert(tipXMax, yMin, tipZmin, 0, 8);
        Vertex eus = renderer.vert(xMax,    yMax, zMin,    8, 8);
        Vertex wus = renderer.vert(xMin,    yMax, zMin,    8, 0);
        Vertex wdn = renderer.vert(tipXmin, yMin, tipZMax, 0, 0);
        Vertex edn = renderer.vert(tipXMax, yMin, tipZMax, 0, 8);
        Vertex eun = renderer.vert(xMax,    yMax, zMax,    8, 8);
        Vertex wun = renderer.vert(xMin,    yMax, zMax,    8, 0);

        polygons = new Quad[] {
            renderer.quad(texX + d + w,     d, texY + d,  h, edn, eds, eus, eun),
            renderer.quad(texX,             d, texY + d,  h, wds, wdn, wun, wus),
            renderer.quad(texX + d,         w, texY,      d, edn, wdn, wds, eds),
            renderer.quad(texX + d + w,     w, texY + d, -d, eus, wus, wun, eun),
            renderer.quad(texX + d,         w, texY + d,  h, eds, wds, wus, eus),
            renderer.quad(texX + d + w + d, w, texY + d,  h, wdn, edn, eun, wun)
        };

        if (renderer.mirror) {
            for (Quad i : polygons) {
                i.flip();
            }
        }
    }

    @Override
    public void render(BufferBuilder buffer, float scale) {
        for (Quad i : polygons) {
            i.render(buffer, scale);
        }
    }
}