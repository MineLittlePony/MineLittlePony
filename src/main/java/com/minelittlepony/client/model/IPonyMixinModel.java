package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.mson.api.ModelView;
import com.minelittlepony.mson.api.model.BoxBuilder.RenderLayerSetter;

public interface IPonyMixinModel<T extends LivingEntity, M extends PonyModel<T>> extends PonyModel<T> {

    M mixin();

    @Override
    default void init(ModelView context) {
        mixin().init(context);
        if (mixin() instanceof RenderLayerSetter && this instanceof RenderLayerSetter) {
            ((RenderLayerSetter)this).setRenderLayerFactory(((RenderLayerSetter)mixin()).getRenderLayerFactory());
        }
    }

    @Override
    default void updateLivingState(T entity, Pony pony, ModelAttributes.Mode mode) {
        mixin().updateLivingState(entity, pony, mode);
    }

    @Override
    default void copyAttributes(BipedEntityModel<T> other) {
        mixin().copyAttributes(other);
    }

    @Override
    default void transform(BodyPart part, MatrixStack stack) {
        mixin().transform(part, stack);
    }

    @Override
    default ModelAttributes getAttributes() {
        return mixin().getAttributes();
    }

    @Override
    default Size getSize() {
        return mixin().getSize();
    }

    @Override
    default void setMetadata(PonyData meta) {
        mixin().setMetadata(meta);
    }

    @Override
    default float getSwingAmount() {
        return mixin().getSwingAmount();
    }

    @Override
    default float getWobbleAmount() {
        return mixin().getWobbleAmount();
    }

    @Override
    default float getRiderYOffset() {
        return mixin().getRiderYOffset();
    }

    @Override
    default void setArmAngle(Arm arm, MatrixStack stack) {
        if (mixin() instanceof ModelWithArms) {
            ((ModelWithArms)mixin()).setArmAngle(arm, stack);
        }
    }

    @Override
    default ModelPart getHead() {
        return mixin().getHead();
    }

    @Override
    default ModelPart getBodyPart(BodyPart part) {
        return mixin().getBodyPart(part);
    }

    @Override
    default void setHatVisible(boolean hatVisible) {
        mixin().setHatVisible(hatVisible);
    }

    interface Caster<T extends LivingEntity, M extends PonyModel<T> & HornedPonyModel<T>, ArmModel> extends IPonyMixinModel<T, M>, HornedPonyModel<T> {
        @Override
        default boolean isCasting() {
            return mixin().isCasting();
        }
    }
}
