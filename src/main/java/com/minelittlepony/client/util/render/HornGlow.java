package com.minelittlepony.client.util.render;

import net.minecraft.client.render.BufferBuilder;

/**
 * Like a normal box, but with the top narrowed a bit.
 */
public class HornGlow extends Box<GlowRenderer> {

    private final float alpha;

    private Quad[] polygons;

    public HornGlow(GlowRenderer renderer, int texX, int texY, float xMin, float yMin, float zMin, int w, int h, int d, float scale, float alpha) {
        super(renderer, texX, texY, xMin, yMin, zMin, w, h, d, scale);

        this.alpha = alpha;

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
        Vertex wds = vert(tipXmin, yMin, tipZmin, 0, 0);
        Vertex eds = vert(tipXMax, yMin, tipZmin, 0, 8);
        Vertex eus = vert(xMax,    yMax, zMin,    8, 8);
        Vertex wus = vert(xMin,    yMax, zMin,    8, 0);
        Vertex wdn = vert(tipXmin, yMin, tipZMax, 0, 0);
        Vertex edn = vert(tipXMax, yMin, tipZMax, 0, 8);
        Vertex eun = vert(xMax,    yMax, zMax,    8, 8);
        Vertex wun = vert(xMin,    yMax, zMax,    8, 0);

        polygons = new Quad[] {
            quad(texX + d + w,     d, texY + d,  h, edn, eds, eus, eun),
            quad(texX,             d, texY + d,  h, wds, wdn, wun, wus),
            quad(texX + d,         w, texY,      d, edn, wdn, wds, eds),
            quad(texX + d + w,     w, texY + d, -d, eus, wus, wun, eun),
            quad(texX + d,         w, texY + d,  h, eds, wds, wus, eus),
            quad(texX + d + w + d, w, texY + d,  h, wdn, edn, eun, wun)
        };

        if (renderer.mirror) {
            for (Quad i : polygons) {
                i.flip();
            }
        }
    }

    @Override
    public void render(BufferBuilder buffer, float scale) {
        parent.applyTint(alpha);

        for (Quad i : polygons) {
            i.render(buffer, scale);
        }
    }
}
