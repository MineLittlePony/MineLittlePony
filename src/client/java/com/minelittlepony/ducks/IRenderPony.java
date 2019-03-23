package com.minelittlepony.ducks;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.render.RenderPony;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * I Render Pony now, oky?
 */
public interface IRenderPony<T extends EntityLivingBase> extends PonyModelConstants {

    /**
     * Gets the wrapped pony model for this renderer.
     */
    ModelWrapper getModelWrapper();

    IPony getEntityPony(T entity);

    RenderPony<T> getInternalRenderer();

    ResourceLocation getTexture(T entity);

    /**
     * Called by riders to have their transportation adjust their position.
     */
    default void translateRider(T entity, IPony entityPony, EntityLivingBase passenger, IPony passengerPony, float ticks) {
        if (!passengerPony.getRace(false).isHuman()) {
            float yaw = MathUtil.interpolateDegress(entity.prevRenderYawOffset, entity.renderYawOffset, ticks);

            getModelWrapper().apply(entityPony.getMetadata());
            IModel model = getModelWrapper().getBody();

            model.transform(BodyPart.BACK);

            getInternalRenderer().applyPostureRiding(entity, yaw, ticks);
        }
    }
}
