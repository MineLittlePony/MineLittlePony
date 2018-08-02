package com.minelittlepony.gui;

import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

public class Slider extends GuiSlider {

    private static Responder callback;

    public Slider(int x, int y, float minIn, float maxIn, float defaultValue, GuiSlider.FormatHelper formatter, IGUIAction<Float> action) {
        super(callback = new Responder(action), 0, x, y, "", minIn, maxIn, defaultValue, formatter);
        callback.owner = this;
        callback = null;
    }

    private static final class Responder implements GuiResponder {

        private final IGUIAction<Float> action;

        private Slider owner;

        private Responder(IGUIAction<Float> callback) {
            action = callback;
        }

        @Override
        public void setEntryValue(int id, boolean value) {
        }

        @Override
        public void setEntryValue(int id, float value) {
            owner.setSliderValue(action.perform(value), false);
        }

        @Override
        public void setEntryValue(int id, String value) {
        }

    }
}
