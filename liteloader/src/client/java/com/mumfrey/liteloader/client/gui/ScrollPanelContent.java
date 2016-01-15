package com.mumfrey.liteloader.client.gui;

import net.minecraft.client.gui.GuiButton;

public interface ScrollPanelContent
{
    public abstract int getScrollPanelContentHeight(GuiScrollPanel source);

    public abstract void drawScrollPanelContent(GuiScrollPanel source, int mouseX, int mouseY, float partialTicks, int scrollAmt, int visibleHeight);

    public abstract void scrollPanelActionPerformed(GuiScrollPanel source, GuiButton control);

    public abstract void scrollPanelMousePressed(GuiScrollPanel source, int mouseX, int mouseY, int mouseButton);
}
