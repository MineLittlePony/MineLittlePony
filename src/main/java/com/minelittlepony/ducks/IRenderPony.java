package com.minelittlepony.ducks;

import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.IPony;

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
}
