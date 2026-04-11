package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.client.render.entity.StriderRenderer;

public class SaddleModel extends EntityModel<StriderRenderer.State> {
    public SaddleModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void setupAnim(StriderRenderer.State entity) {
        root.y = 2 - Mth.cos(entity.walkAnimationSpeed * 1.5F) * 3 * entity.walkAnimationPos;
    }
}
