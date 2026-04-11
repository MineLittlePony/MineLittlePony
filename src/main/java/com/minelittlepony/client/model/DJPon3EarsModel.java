package com.minelittlepony.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.skull.SkullModel;

public class DJPon3EarsModel extends SkullModel {

    public DJPon3EarsModel(ModelPart tree) {
        super(tree);
    }

    public void setVisible(boolean show) {
        head.visible = show;
    }
}
