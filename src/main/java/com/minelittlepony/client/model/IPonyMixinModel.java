package com.minelittlepony.client.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.model.ModelAttributes;
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
    default IPonyData getMetadata() {
        return mixin().getMetadata();
    }

    @Override
    default ModelAttributes<?> getAttributes() {
        return mixin().getAttributes();
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
    default void apply(IPonyData meta) {
        mixin().apply(meta);
    }

    @Override
    default boolean isRiding() {
        return mixin().isRiding();
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
    default void setArmAngle(float var1, AbsoluteHand var2) {
        mixin().setArmAngle(var1, var2);
    }

    @Override
    default Cuboid getHead() {
        return mixin().getHead();
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
