package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GuardianEntity;

import com.minelittlepony.client.model.IPonyMixinModel;

public class ModelGuardianPony extends GuardianEntityModel implements IPonyMixinModel.Caster<GuardianEntity, ModelSeapony<GuardianEntity>, ModelPart> {
    private final ModelSeapony<GuardianEntity> mixin = new ModelSeapony<>();

    @Override
    public void setAngles(GuardianEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        mixin().setAngles(entity, move, swing, ticks, headYaw, headPitch);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float limbDistance, float limbAngle, float tickDelta, float alpha) {
        mixin().render(stack, vertices, overlayUv, lightUv, limbDistance, limbAngle, tickDelta, alpha);
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
