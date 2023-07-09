package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.MatrixStack;
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

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        mixin().render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void animateModel(GuardianEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        mixin().animateModel(entity, limbAngle, limbDistance, tickDelta);
    }

    @Override
    public void copyStateTo(EntityModel<GuardianEntity> copy) {
        mixin().copyStateTo(copy);
    }

    @Override
    public void setAngles(GuardianEntity entity, float limbAngle, float limbSpeed, float animationProgress, float headYaw, float headPitch) {
        mixin().setVisible(true);
        mixin().setAngles(entity, limbAngle, limbSpeed, animationProgress, headYaw, headPitch);
    }
}
