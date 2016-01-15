package com.mumfrey.liteloader;

import net.minecraft.client.gui.ScaledResolution;

public interface ViewportListener extends LiteMod
{
    public abstract void onViewportResized(ScaledResolution resolution, int displayWidth, int displayHeight);

    public abstract void onFullScreenToggled(boolean fullScreen);
}
