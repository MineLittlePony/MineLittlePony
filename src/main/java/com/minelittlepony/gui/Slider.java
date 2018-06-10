package com.minelittlepony.gui;

import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

import java.util.function.DoubleUnaryOperator;

public class Slider extends GuiSlider {

    private static Responder callback;

    public Slider(int x, int y, float minIn, float maxIn, float defaultValue, GuiSlider.FormatHelper formatter, DoubleUnaryOperator action) {
        super(callback = new Responder(action), 0, x, y, "", minIn, maxIn, defaultValue, formatter);
        callback.owner = this;
        callback = null;
    }

    private static final class Responder implements GuiResponder {

        private final DoubleUnaryOperator action;

        private Slider owner;

        private Responder(DoubleUnaryOperator callback) {
            action = callback;
        }

        @Override
        public void setEntryValue(int id, boolean value) { }

        @Override
        public void setEntryValue(int id, float value) {
            owner.setSliderValue((float) action.applyAsDouble(value), false);
        }

        @Override
        public void setEntryValue(int id, String value) { }

    }
}
