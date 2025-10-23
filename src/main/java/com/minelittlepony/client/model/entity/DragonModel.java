package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class DragonModel<T extends BipedEntityRenderState> extends BipedEntityModel<T> {

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
