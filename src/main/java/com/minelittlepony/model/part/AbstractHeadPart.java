package com.minelittlepony.model.part;

import com.minelittlepony.PonyData;
import com.minelittlepony.model.AbstractPonyModel;

public abstract class AbstractHeadPart implements IPonyPart {

    protected final AbstractPonyModel pony;

    public AbstractHeadPart(AbstractPonyModel pony) {
        this.pony = pony;
    }

    @Override
    public void render(PonyData data, float scale) {}

    @Override
    public void animate(PonyData metadata, float move, float swing, float tick, float horz, float vert) {}
}
