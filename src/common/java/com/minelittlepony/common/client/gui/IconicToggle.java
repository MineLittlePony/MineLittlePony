package com.minelittlepony.common.client.gui;

import com.minelittlepony.common.util.MoreStreams;

public class IconicToggle extends IconicButton {

    private Style[] styles;

    private int value;

    public IconicToggle(int x, int y, int states, IGuiAction<IconicToggle> callback) {
        super(x, y, callback);
        styles = new Style[states];
        for (int i = 0; i < styles.length; i++) {
            styles[i] = new Style();
        }
    }

    public int getValue() {
        return value;
    }

    public IconicToggle setValue(int value) {
        if (this.value != value) {
            this.value = value % styles.length;
            styles[this.value].apply(this);
        }

        return this;
    }

    public IconicToggle setStyles(IStyleFactory... styles) {
        this.styles = MoreStreams.map(styles, IStyleFactory::getStyle, Style[]::new);

        return this;
    }

    public IconicToggle setStyles(Style... styles) {
        this.styles = styles;

        return this;
    }

    public IconicToggle setStyle(Style style, int value) {
        value %= styles.length;

        styles[value] = style;

        if (this.value == value) {
            style.apply(this);
        }

        return this;
    }

    @Override
    public void perform() {
        setValue(value + 1);
        super.perform();
    }
}
