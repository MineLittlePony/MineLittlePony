package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.mob.GuardianEntity;
import com.minelittlepony.client.model.IPonyMixinModel;
import com.minelittlepony.client.model.entity.race.SeaponyModel;

public class GuardianPonyModel extends GuardianEntityModel implements IPonyMixinModel.Caster<GuardianEntity, SeaponyModel<GuardianEntity>, ModelPart> {
    private final SeaponyModel<GuardianEntity> mixin;

    public GuardianPonyModel(ModelPart tree) {
        super(getTexturedModelData().createModel());
        mixin = new SeaponyModel<>(tree);
    }

    @Override
    public SeaponyModel<GuardianEntity> mixin() {
        return mixin;
    }
}
