package com.minelittlepony.client.model.entity.race;

import net.minecraft.entity.LivingEntity;

@Deprecated
public class ModelBatpony<T extends LivingEntity> extends ModelPegasus<T> {
    public ModelBatpony(boolean smallArms) {
        super(smallArms);
    }
}
