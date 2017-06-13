package com.minelittlepony.renderer;

import com.minelittlepony.model.ModelPlane;
import com.minelittlepony.model.ModelPlane.Face;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SuppressWarnings("unused")
public class PlaneRenderer extends ModelRenderer {

    public boolean mirrory;
    public boolean mirrorz;

    private int textureOffsetX;
    private int textureOffsetY;

    public PlaneRenderer(ModelBase model) {
        super(model);
    }

    public PlaneRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
    }

    @Override
    public PlaneRenderer setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        super.setTextureOffset(x, y);
        return this;
    }

    public void addPlane(float offX, float offY, float offZ, int width, int height, int depth, float scale, Face face) {
        this.cubeList.add(new ModelPlane(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scale, face));
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
