package com.minelittlepony.renderer.plane;

import com.minelittlepony.renderer.BasePonyRenderer;

import net.minecraft.client.model.ModelBase;

public class PlaneRenderer extends BasePonyRenderer<PlaneRenderer> {

    public boolean mirrory, mirrorz;

    public PlaneRenderer(ModelBase model) {
        super(model);
    }

    public PlaneRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    private void addPlane(float offX, float offY, float offZ, int width, int height, int depth, float scale, Face face) {
        this.cubeList.add(new ModelPlane(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scale, face));
    }

    public void addTopPlane(float offX, float offY, float offZ, int width, int depth, float scale) {
        this.addPlane(offX, offY, offZ, width, 0, depth, scale, Face.UP);
    }

    public void addBottomPlane(float offX, float offY, float offZ, int width, int depth, float scale) {
        this.addPlane(offX, offY, offZ, width, 0, depth, scale, Face.DOWN);
    }

    public void addWestPlane(float offX, float offY, float offZ, int height, int depth, float scale) {
        this.addPlane(offX, offY, offZ, 0, height, depth, scale, Face.WEST);
    }

    public void addEastPlane(float offX, float offY, float offZ, int height, int depth, float scale) {
        this.addPlane(offX, offY, offZ, 0, height, depth, scale, Face.EAST);
    }

    public void addFrontPlane(float offX, float offY, float offZ, int width, int height, float scale) {
        this.addPlane(offX, offY, offZ, width, height, 0, scale, Face.NORTH);
    }

    public void addBackPlane(float offX, float offY, float offZ, int width, int height, float scale) {
        this.addPlane(offX, offY, offZ, width, height, 0, scale, Face.SOUTH);
    }
}
