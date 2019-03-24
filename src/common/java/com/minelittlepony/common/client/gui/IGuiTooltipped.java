package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;

import com.google.common.base.Splitter;

import java.util.List;

/**
 * Interface element that renders a tooltip when hovered.
 *
 * @author Sollace
 *
 * @param <T> The subclass element.
 */
public interface IGuiTooltipped<T extends IGuiTooltipped<T>> {

    /**
     * Sets the tooltip text with a multi-line value.
     */
    T setTooltip(List<String> tooltip);

    /**
     * Sets the tooltip offset from the original mouse position.
     */
    T setTooltipOffset(int x, int y);

    /**
     * Sets the tooltip. The passed in value will be automatically
     * translated and split into separate lines.
     *
     * @param tooltip A tooltip translation string.
     */
    default T setTooltip(String tooltip) {
        return setTooltip(Splitter.onPattern("\r?\n|\\\\n").splitToList(GameGui.format(tooltip)));
    }

    /**
     * Draws this element's tooltip.
     */
    void renderToolTip(Minecraft mc, int mouseX, int mouseY);
}
