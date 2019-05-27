package com.minelittlepony.client.model.components;

import net.minecraft.client.render.entity.model.SkullEntityModel;

public class ModelDeadMau5Ears extends SkullEntityModel {

    public ModelDeadMau5Ears() {
        super(24, 0, 64, 64);
        field_3564.boxes.clear();
        field_3564.addBox(-9, -13, -1, 6, 6, 1, 0);
        field_3564.addBox(3, -13, -1, 6, 6, 1, 0);
    }

    public void setVisible(boolean show) {
        field_3564.field_3664 = !show;
    }
}
