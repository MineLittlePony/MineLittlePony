package com.minelittlepony.client.render;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.model.gear.IRenderContext;
import com.minelittlepony.util.MathUtil;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface IPonyRenderContext<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends PonyModelConstants, IRenderContext<T, M> {

    /**
     * Gets the wrapped pony model for this renderer.
     */
    ModelWrapper<T, M> getModelWrapper();

    IPony getEntityPony(T entity);

    EquineRenderManager<T, M> getInternalRenderer();

    Identifier findTexture(T entity);

    @Override
    default Identifier getDefaultTexture(T entity, Wearable wearable) {
        return findTexture(entity);
    }

    /**
     * Called by riders to have their transportation adjust their position.
     */
    default void translateRider(T entity, IPony entityPony, LivingEntity passenger, IPony passengerPony, MatrixStack stack, float ticks) {
        if (!passengerPony.getRace(false).isHuman()) {
            float yaw = MathUtil.interpolateDegress((float)entity.prevY, (float)entity.getY(), ticks);

            getModelWrapper().apply(entityPony.getMetadata());
            M model = getModelWrapper().getBody();

            model.transform(BodyPart.BACK, stack);

            getInternalRenderer().applyPostureRiding(entity, stack, yaw, ticks);
        }
    }
}
