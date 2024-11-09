package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

public class SaddleModel<T extends LivingEntityRenderState> extends EntityModel<T> {
    public SaddleModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void setAngles(T entity) {
        root.pivotY = 2 - MathHelper.cos(entity.limbFrequency * 1.5F) * 3 * entity.limbAmplitudeMultiplier;
    }
}
