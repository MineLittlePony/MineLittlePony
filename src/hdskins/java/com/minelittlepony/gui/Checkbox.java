package com.minelittlepony.gui;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import net.minecraft.client.resources.I18n;

/**
 * Checkbox that supports a gui action when it changes.
 *
 * @author Sollace
 *
 */
public class Checkbox extends GuiCheckbox implements IActionable {

    private final IGuiCallback<Boolean> action;

    public Checkbox(int x, int y, String displayString, boolean initial, IGuiCallback<Boolean> callback) {
        super(0, x, y, I18n.format(displayString));
        action = callback;
        checked = initial;
    }

    @Override
    public void perform() {
        action.perform(checked ^= true);
    }

}
