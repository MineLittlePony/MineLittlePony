package com.voxelmodpack.common.interfaces;

public interface IRegionRenderer {
    /**
     * Render a 3-dimensional region using the currently bound texture
     * 
     * @param x1 First x coordinate
     * @param y1 First y coordinate
     * @param z1 First z coordinate
     * @param x2 Second x coordinate
     * @param y2 Second y coordinate
     * @param z2 Second z coordinate
     */
    public abstract void renderRegion3D(int x1, int y1, int z1, int x2, int y2, int z2);

    /**
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public abstract void glColor4f(float red, float green, float blue, float alpha);
}