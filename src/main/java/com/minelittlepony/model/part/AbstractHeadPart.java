package com.minelittlepony.model.part;

import com.minelittlepony.PonyData;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public abstract class AbstractHeadPart implements IPonyPart {

    protected final AbstractPonyModel pony;

    public AbstractHeadPart(AbstractPonyModel pony) {
        this.pony = pony;
    }

    @Override
    public void render(PonyData data, float scale) {
        pony.transform(BodyPart.HEAD);
        pony.bipedHead.postRender(scale);
    }

    @Override
    public void animate(PonyData metadata, float move, float swing, float tick, float horz, float vert) {

    }
}
