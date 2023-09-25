package com.minelittlepony.client.render;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface IPonyRenderContext<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends Gear.Context<T, M> {

    Pony getEntityPony(T entity);

    EquineRenderManager<T, M> getInternalRenderer();

    /**
     * Called by riders to have their transportation adjust their position.
     */
    default void translateRider(T entity, Pony entityPony, LivingEntity passenger, Pony passengerPony, MatrixStack stack, float ticks) {
        if (!passengerPony.race().isHuman()) {
            float yaw = MathUtil.interpolateDegress((float)entity.prevY, (float)entity.getY(), ticks);

            getInternalRenderer().getModelWrapper().applyMetadata(entityPony.metadata());
            M model = getInternalRenderer().getModelWrapper().body();

            model.transform(BodyPart.BACK, stack);

            getInternalRenderer().applyPostureRiding(entity, stack, yaw, ticks);
        }
    }
}
