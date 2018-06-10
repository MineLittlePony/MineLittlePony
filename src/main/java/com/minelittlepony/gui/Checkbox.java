package com.minelittlepony.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.util.function.Consumer;

public class Checkbox extends GuiCheckBox implements IActionable {

    private final Consumer<Boolean> callback;

    public Checkbox(int x, int y, String displayString, boolean value, Consumer<Boolean> callback) {
        super(0, x, y, I18n.format(displayString), value);
        this.callback = callback;
    }

    @Override
    public void perform() {
        this.callback.accept(isChecked());
    }

}
