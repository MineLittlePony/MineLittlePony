package com.minelittlepony.common.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * Checkbox that supports a gui action when it changes.
 *
 * @author Sollace
 *
 */
public class Checkbox extends Slider implements IGuiTooltipped<Checkbox> {

    protected static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/gui/widgets.png");

    private int tipX = 0;
    private int tipY = 0;

    private List<String> tooltip = null;

    private boolean checked;

    private IGuiCallback<Boolean> switchAction;

    public Checkbox(int x, int y, String displayString, boolean value, IGuiCallback<Boolean> callback) {
        super(x, y, 0, 1, (value ? 1 : 0), (i, name, v) -> I18n.format(displayString), null);

        checked = value;

        width = 20;
        height = 20;

        switchAction = callback;
        action = this::perform;
    }

    @Override
    public void setSliderValue(float value, boolean notifyResponder) {
        super.setSliderValue(value >= 0.5F ? 1 : 0, notifyResponder);
    }

    protected float perform(float v) {
        boolean value = v >= 0.5F;

        if (value != checked) {
            checked = switchAction.perform(value);
        }

        return checked ? 1 : 0;
    }

    @Override
    public Checkbox setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void renderToolTip(Minecraft mc, int mouseX, int mouseY) {
        if (visible && isMouseOver() && tooltip != null) {
            mc.currentScreen.drawHoveringText(tooltip, mouseX + tipX, mouseY + tipY);
        }
    }

    @Override
    public Checkbox setTooltipOffset(int x, int y) {
        tipX = x;
        tipY = y;
        return this;
    }

    @Override
    public void drawCenteredString(FontRenderer fonts, String text, int x, int y, int color) {
        super.drawString(fonts, text, x + width - 5, y, color);
    }
}
