package com.mumfrey.liteloader.client.overlays;

import com.mumfrey.liteloader.transformers.access.Accessor;

/**
 * Adapter for GuiTextField to expose internal properties, mainly to allow
 * sensible subclassing.
 * 
 * @author Adam Mummery-Smith
 */
@Accessor("GuiTextField")
public interface IGuiTextField
{
    @Accessor("#2")  public abstract int     getXPosition();
    @Accessor("#2")  public abstract void    setXPosition(int xPosition);

    @Accessor("#3")  public abstract int     getYPosition();
    @Accessor("#3")  public abstract void    setYPosition(int yPosition);

    @Accessor("#4")  public abstract int     getInternalWidth();
    @Accessor("#4")  public abstract void    setInternalWidth(int width);

    @Accessor("#5")  public abstract int     getHeight();
    @Accessor("#5")  public abstract void    setHeight(int height);

    @Accessor("#12") public abstract boolean isEnabled();
//    @Accessor("#12") public abstract void    setEnabled(boolean enabled); // built in

    @Accessor("#13") public abstract int     getLineScrollOffset();

    @Accessor("#16") public abstract int     getTextColor();
//    @Accessor("#16") public abstract void    setTextColor(int color); // built in

    @Accessor("#17") public abstract int     getDisabledTextColour();
//    @Accessor("#17") public abstract void    setDisabledTextColour(int color); // built in
}
