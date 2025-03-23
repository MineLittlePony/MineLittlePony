package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.render.entity.StriderRenderer;

public class SaddleModel extends EntityModel<StriderRenderer.State> {
    public SaddleModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void setAngles(StriderRenderer.State entity) {
        root.originY = 2 - MathHelper.cos(entity.limbSwingAnimationProgress * 1.5F) * 3 * entity.limbSwingAmplitude;
    }
}
