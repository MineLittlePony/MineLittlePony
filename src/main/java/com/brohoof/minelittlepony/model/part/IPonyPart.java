package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.AbstractPonyModel;

public interface IPonyPart {

    void init(AbstractPonyModel pony, float yOffset, float stretch);

    void animate(PonyData metadata, float move, float moveswing, float loop, float right, float down);

    void render(PonyData data, float scale);

}
