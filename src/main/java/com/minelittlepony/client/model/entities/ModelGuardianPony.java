package com.minelittlepony.client.model.entities;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.mob.GuardianEntity;

import com.minelittlepony.client.model.IPonyMixinModel;
import com.minelittlepony.client.util.render.PonyRenderer;

public class ModelGuardianPony extends GuardianEntityModel implements IPonyMixinModel.Caster<GuardianEntity, ModelSeapony<GuardianEntity>, PonyRenderer> {
    private final ModelSeapony<GuardianEntity> mixin = new ModelSeapony<>();

    @Override
    public void setAngles(GuardianEntity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        mixin().setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    @Override
    public void render(GuardianEntity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        mixin().render(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    @Override
    public void animateModel(GuardianEntity entity, float move, float swing, float float_3) {
        mixin().animateModel(entity, move, swing, float_3);
    }

    @Override
    public void copyStateTo(EntityModel<GuardianEntity> model) {
        mixin().copyStateTo(model);
    }

    @Override
    public ModelSeapony<GuardianEntity> mixin() {
        return mixin;
    }
}
