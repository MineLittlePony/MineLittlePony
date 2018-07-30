package com.minelittlepony.gui;

import com.minelittlepony.settings.Value;
import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import net.minecraft.client.resources.I18n;

public class Checkbox extends GuiCheckbox implements IActionable {

    private final Value<Boolean> setting;

    public Checkbox(int x, int y, String displayString, Value<Boolean> setting) {
        super(0, x, y, I18n.format(displayString));
        this.setting = setting;
        checked = setting.get();
    }

    @Override
    public void perform() {
        setting.set(checked ^= true);
    }

}
