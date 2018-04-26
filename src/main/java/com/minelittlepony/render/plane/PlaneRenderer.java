package com.minelittlepony.render.plane;

import com.minelittlepony.render.BasePonyRenderer;

import net.minecraft.client.model.ModelBase;

public class PlaneRenderer extends BasePonyRenderer<PlaneRenderer> {

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

    private PlaneRenderer addPlane(float offX, float offY, float offZ, int width, int height, int depth, float scale, Face face) {
        cubeList.add(new ModelPlane(this, textureOffsetX, textureOffsetY, modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scale, face));
        return this;
    }

    public PlaneRenderer addTopPlane(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Face.UP);
    }

    public PlaneRenderer addBottomPlane(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Face.DOWN);
    }

    public PlaneRenderer addWestPlane(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Face.WEST);
    }

    public PlaneRenderer addEastPlane(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Face.EAST);
    }

    public PlaneRenderer addFrontPlane(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ, width, height, 0, scale, Face.NORTH);
    }

    public PlaneRenderer addBackPlane(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ, width, height, 0, scale, Face.SOUTH);
    }
}
