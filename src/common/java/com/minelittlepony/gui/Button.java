package com.minelittlepony.gui;

import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton implements IActionable {

    private IGUIAction<Button> action;

    public Button(int x, int y, int width, int height, String label, IGUIAction<Button> callback) {
        super(0, x, y, width, height, GameGui.translate(label));
        action = callback;
    }

    @Override
    public void perform() {
        action.perform(this);
    }

}
