package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.Pivot;

public class KirinModel<T extends LivingEntity> extends UnicornModel<T> {

    private final ModelPart beard;

    public KirinModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        beard = neck.getChild("beard");
    }

    @Override
    protected void adjustBody(float pitch, Pivot pivot) {
        super.adjustBody(pitch, pivot);
        beard.resetTransform();
        beard.pitch -= neck.pitch;
    }
}
