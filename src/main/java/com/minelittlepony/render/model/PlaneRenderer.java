package com.minelittlepony.render.model;

import net.minecraft.client.model.ModelBase;

import com.minelittlepony.util.render.AbstractBoxRenderer;

public class PlaneRenderer extends AbstractBoxRenderer<PlaneRenderer> {

    public boolean mirrory, mirrorz;

    public PlaneRenderer(ModelBase model) {
        super(model);
    }

    public PlaneRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    /**
     * Flips the Z bit. Any calls to add a plane will be mirrored until this is called again.
     */
    public PlaneRenderer flipZ() {
        mirrorz = !mirrorz;
        return this;
    }


    /**
     * Flips the Y bit. Any calls to add a plane will be mirrored until this is called again.
     */
    public PlaneRenderer flipY() {
        mirrory = !mirrory;
        return this;
    }

    @Override
    protected PlaneRenderer copySelf() {
        return new PlaneRenderer(baseModel, textureOffsetX, textureOffsetY);
    }

    private PlaneRenderer addPlane(float offX, float offY, float offZ, int width, int height, int depth, float scale, Plane face) {
        cubeList.add(new ModelPlane(this, textureOffsetX, textureOffsetY, modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scale, face));
        return this;
    }

    public PlaneRenderer top(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Plane.UP);
    }

    public PlaneRenderer bottom(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Plane.DOWN);
    }

    public PlaneRenderer west(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Plane.WEST);
    }

    public PlaneRenderer east(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Plane.EAST);
    }

    public PlaneRenderer north(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ - scale * 2, width, height, 0, scale, Plane.NORTH);
    }

    public PlaneRenderer south(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ + scale * 2, width, height, 0, scale, Plane.SOUTH);
    }
}
