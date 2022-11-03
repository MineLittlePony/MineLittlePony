package com.minelittlepony.api.pony;

import java.util.List;
import java.util.Objects;

public class TriggerPixelValue<T> implements TriggerPixelType<T> {

    private final int color;
    private final T value;

    public TriggerPixelValue(int color, T value) {
        this.color = color;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String name() {
        return value instanceof TriggerPixelType ? ((TriggerPixelType<T>)value).name() : TriggerPixelType.super.name();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Option extends TriggerPixelType<T>> List<Option> getOptions() {
        return value instanceof TriggerPixelType ? ((TriggerPixelType<T>)value).getOptions() : TriggerPixelType.super.getOptions();
    }

    public T getValue() {
        return value;
    }

    @Override
    public int getColorCode() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o)
            || (o instanceof TriggerPixelValue && Objects.equals(((TriggerPixelValue<?>)o).getValue(), getValue()))
            || Objects.equals(o, getValue());
    }
}
