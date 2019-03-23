package com.minelittlepony.common.client.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiHost extends GameGui {

    private final IGuiGuest guest;

    public GuiHost(IGuiGuest guest) {
        this.guest = guest;
    }

    @Override
    public void initGui() {
        guest.initGui(this);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks) {
        if (guest.drawContents(this, mouseX, mouseY, partialTicks)) {
            super.drawContents(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onGuiClosed() {
        guest.onGuiClosed(this);
    }

    public String getTitle() {
        return guest.getTitle();
    }

    @Override
    public <T extends GuiButton> T addButton(T button) {
        return super.addButton(button);
    }

    public boolean mustScroll() {
        return false;
    }
}
