package com.minelittlepony.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;

import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.pony.IPonyData;
import com.minelittlepony.pony.meta.Size;

public interface IPonyMixinModel<T extends LivingEntity, M extends IPonyModel<T>> extends IPonyModel<T> {

    M mixin();

    @Override
    default void init(float yOffset, float stretch) {
        mixin().init(yOffset, stretch);
    }

    @Override
    default void transform(BodyPart part) {
        mixin().transform(part);
    }

    @Override
    default void setPitch(float pitch) {
        mixin().setPitch(pitch);
    }

    @Override
    default float getPitch() {
        return mixin().getPitch();
    }

    @Override
    default Size getSize() {
        return mixin().getSize();
    }

    @Override
    default IEquestrianArmour<?> createArmour() {
        return mixin().createArmour();
    }

    @Override
    default IPonyData getMetadata() {
        return mixin().getMetadata();
    }

    @Override
    default void apply(IPonyData meta) {
        mixin().apply(meta);
    }

    @Override
    default boolean isCrouching() {
        return mixin().isCrouching();
    }

    @Override
    default boolean isFlying() {
        return mixin().isFlying();
    }

    @Override
    default boolean isElytraFlying() {
        return mixin().isElytraFlying();
    }

    @Override
    default boolean isSleeping() {
        return mixin().isSleeping();
    }

    @Override
    default boolean isSwimming() {
        return mixin().isSwimming();
    }

    @Override
    default boolean isRiding() {
        return mixin().isRiding();
    }

    @Override
    default boolean isGoingFast() {
        return mixin().isGoingFast();
    }

    @Override
    default boolean isChild() {
        return mixin().isChild();
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
    default float getModelHeight() {
        return mixin().getModelHeight();
    }

    @Override
    default void setArmAngle(float var1, AbsoluteHand var2) {
        mixin().setArmAngle(var1, var2);
    }

    @Override
    default Cuboid getHead() {
        return mixin().getHead();
    }

    @Override
    default boolean hasHeadGear() {
        return mixin().hasHeadGear();
    }

    @Override
    default Cuboid getBodyPart(BodyPart part) {
        return mixin().getBodyPart(part);
    }

    interface Caster<T extends LivingEntity, M extends IPonyModel<T> & IUnicorn<Arm>, Arm> extends IPonyMixinModel<T, M>, IUnicorn<Arm> {

        @Override
        default Arm getUnicornArmForSide(AbsoluteHand side) {
            return mixin().getUnicornArmForSide(side);
        }

        @Override
        default boolean isCasting() {
            return mixin().isCasting();
        }
    }
}
