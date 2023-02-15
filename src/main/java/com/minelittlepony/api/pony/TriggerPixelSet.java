package com.minelittlepony.api.pony;

import java.util.List;

@SuppressWarnings("unchecked")
public class TriggerPixelSet<T extends Enum<T> & TriggerPixelType<T>> extends TriggerPixelValue<boolean[]> {

    private final T def;

    public TriggerPixelSet(int color, T def, boolean[] value) {
        super(color, value);
        this.def = def;
    }

    @Override
    public List<TriggerPixelType<T>> getOptions() {
        return def.getOptions();
    }

    @Override
    public boolean matches(Object o) {
        return o.getClass() == def.getClass() && getValue()[((Enum<?>)o).ordinal()];
    }
}
