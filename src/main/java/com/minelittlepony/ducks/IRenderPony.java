package com.minelittlepony.ducks;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.render.RenderPony;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.entity.EntityLivingBase;

/**
 * I Render Pony now, oky?
 */
public interface IRenderPony<T extends EntityLivingBase> {

    /**
     * Gets the wrapped pony model for this renderer.
     */
    ModelWrapper getModelWrapper();

    IPony getEntityPony(T entity);

    RenderPony<T> getInternalRenderer();

    /**
     * Called by riders to have their transportation adjust their position.
     */
    default void translateRider(T entity, IPony entityPony, EntityLivingBase passenger, IPony passengerPony, float ticks) {
        if (!passengerPony.getRace(false).isHuman()) {
            float yaw = MathUtil.interpolateDegress(entity.prevRenderYawOffset, entity.renderYawOffset, ticks);

            getModelWrapper().apply(entityPony.getMetadata());
            AbstractPonyModel model = getModelWrapper().getBody();

            model.transform(BodyPart.BACK);

            getInternalRenderer().applyPostureRiding(entity, entity.ticksExisted + ticks, yaw, ticks);
        }
    }
}
