package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class DragonModel<T extends HumanoidRenderState> extends HumanoidModel<T> {

    protected final ModelPart tail;
    protected final ModelPart tail2;
    protected final ModelPart tail3;

    public DragonModel(ModelPart tree) {
        super(tree);
        tail = body.getChild("tail");
        tail2 = tail.getChild("tail2");
        tail3 = tail2.getChild("tail3");
    }
}
