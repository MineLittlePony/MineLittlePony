package com.mumfrey.liteloader.util.render;

public interface Icon
{
    public abstract int getIconWidth();
    public abstract int getIconHeight();
    public abstract float getMinU();
    public abstract float getMaxU();
    public abstract float getInterpolatedU(double slice);
    public abstract float getMinV();
    public abstract float getMaxV();
    public abstract float getInterpolatedV(double slice);
    public abstract String getIconName();
}
