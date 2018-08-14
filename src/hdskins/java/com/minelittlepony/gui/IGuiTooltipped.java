package com.minelittlepony.gui;

import net.minecraft.client.Minecraft;

import com.google.common.base.Splitter;

import java.util.List;

public interface IGuiTooltipped {

    IGuiTooltipped setTooltip(List<String> tooltip);

    default IGuiTooltipped setTooltip(String tooltip) {
        return setTooltip(Splitter.onPattern("\r?\n|\\\\n").splitToList(GameGui.format(tooltip)));
    }

    void renderToolTip(Minecraft mc, int mouseX, int mouseY);
}
