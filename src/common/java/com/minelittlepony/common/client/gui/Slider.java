package com.minelittlepony.common.client.gui;

import net.minecraft.client.gui.GuiSlider;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

/**
 * A slider for sliding.
 *
 * @author Sollace
 *
 */
public class Slider extends GuiSlider {

    private static Responder callback;

    protected IGuiCallback<Float> action;

    public Slider(int x, int y, float minIn, float maxIn, float defaultValue, GuiSlider.FormatHelper formatter, IGuiCallback<Float> action) {
        super(callback = new Responder(), 0, x, y, "", minIn, maxIn, defaultValue, formatter);
        callback.owner = this;
        callback = null;

        this.action = action;
    }

    private static final class Responder implements GuiResponder {

        @Nullable
        private Slider owner;

        @Override
        public void setEntryValue(int id, boolean value) { }

        @Override
        public void setEntryValue(int id, float value) {
            if (owner != null) {
                owner.setSliderValue(owner.action.perform(value), false);
            }
        }

        @Override
        public void setEntryValue(int id, String value) { }

    }
}
