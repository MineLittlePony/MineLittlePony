package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.renderer.AniParams;

public interface IPonyPart {

    void init(ModelPony pony, float yOffset, float stretch);

    void animate(PonyData data, AniParams ani);

    void render(PonyData data, float scale);

}
