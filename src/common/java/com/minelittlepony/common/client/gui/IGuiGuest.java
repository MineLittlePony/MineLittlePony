package com.minelittlepony.common.client.gui;

public interface IGuiGuest {

    void initGui(GuiHost host);

    default boolean drawContents(GuiHost host, int mouseX, int mouseY, float partialTicks) {
        return true;
    }

    default void onGuiClosed(GuiHost host) {

    }

    String getTitle();
}
