package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SkullEntityModel;

public class DJPon3EarsModel extends SkullEntityModel {

    public DJPon3EarsModel(ModelPart tree) {
        super(tree);
    }

    public void setVisible(boolean show) {
        head.visible = show;
    }
}
