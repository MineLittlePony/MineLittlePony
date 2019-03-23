package com.minelittlepony.client.model.components;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;

public class ModelDeadMau5Ears extends ModelSkeletonHead {

    public ModelDeadMau5Ears() {
        super();
        skeletonHead = new ModelRenderer(this, 24, 0);
        skeletonHead.addBox(-9, -13, -1, 6, 6, 1, 0);
        skeletonHead.addBox(3, -13, -1, 6, 6, 1, 0);
    }

    public void setVisible(boolean show) {
        skeletonHead.isHidden = !show;
    }
}
