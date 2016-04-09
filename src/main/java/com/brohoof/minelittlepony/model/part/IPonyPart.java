package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;

public interface IPonyPart {

    void init(float yOffset, float stretch);

    void render(PonyData data, float scale);

    void animate(PonyData metadata, float move, float swing, float tick, float horz, float vert);

}
