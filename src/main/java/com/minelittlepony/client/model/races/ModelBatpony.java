package com.minelittlepony.client.model.races;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.components.BatWings;
import com.minelittlepony.client.model.components.PonyEars;
import com.minelittlepony.client.util.render.PonyRenderer;

public class ModelBatpony<T extends LivingEntity> extends ModelPegasus<T> {

    public ModelBatpony(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected void initWings(float yOffset, float stretch) {
        wings = new BatWings<>(this, yOffset, stretch);
    }

    @Override
    protected void initEars(PonyRenderer head, float yOffset, float stretch) {
        ears = new PonyEars(head, true);
        ears.init(yOffset, stretch);
    }
}
