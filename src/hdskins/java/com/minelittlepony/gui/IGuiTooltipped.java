package com.minelittlepony.gui;

import net.minecraft.client.Minecraft;

import com.google.common.base.Splitter;

import java.util.List;

public interface IGuiTooltipped<T extends IGuiTooltipped<T>> {

    T setTooltip(List<String> tooltip);

    T setTooltipOffset(int x, int y);

    default T setTooltip(String tooltip) {
        return setTooltip(Splitter.onPattern("\r?\n|\\\\n").splitToList(GameGui.format(tooltip)));
    }

    void renderToolTip(Minecraft mc, int mouseX, int mouseY);
}
