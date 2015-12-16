package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.renderer.AniParams;

public interface IPonyPart {

    void init(AbstractPonyModel pony, float yOffset, float stretch);

    void animate(PonyData data, AniParams ani);

    void render(PonyData data, float scale);

}
