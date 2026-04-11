package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.CopperPonyRenderer;

public class CopperPonyModel extends AlicornModel<CopperPonyRenderer.State> {
    public CopperPonyModel(ModelPart tree) {
        super(tree, false);
    }
}
