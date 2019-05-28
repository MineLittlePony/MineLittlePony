package com.minelittlepony.client.model.entities;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.mob.GuardianEntity;

import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IPonyMixinModel;

public class ModelGuardianPony extends GuardianEntityModel implements IPonyMixinModel.Caster<GuardianEntity, ModelSeapony<GuardianEntity>, PonyRenderer> {
    private final ModelSeapony<GuardianEntity> mixin = new ModelSeapony<>();

    @Override
    public void setAngles(GuardianEntity entity_1, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6) {
        mixin().setAngles(entity_1, float_1, float_2, float_3, float_4, float_5, float_6);
    }

    @Override
    public void render(GuardianEntity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
        mixin().render(var1, var2, var3, var4, var5, var6, var7);
    }

    @Override
    public void animateModel(GuardianEntity entity_1, float float_1, float float_2, float float_3) {
        mixin().animateModel(entity_1, float_1, float_2, float_3);
    }

    @Override
    public void copyStateTo(EntityModel<GuardianEntity> entityModel_1) {
        mixin().copyStateTo(entityModel_1);
    }

    @Override
    public ModelSeapony<GuardianEntity> mixin() {
        return mixin;
    }
}
