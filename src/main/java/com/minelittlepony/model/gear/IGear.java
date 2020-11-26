package com.minelittlepony.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.gear.IRenderContext;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.IPart;

public interface IGear extends IPart {

    /**
     * Determines if this wearable can and is worn by the selected entity.
     *
     * @param model     The primary model
     * @param entity    The entity being rendered
     *
     * @return True to render this wearable
     */
    boolean canRender(IModel model, Entity entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    /**
     * Gets the texture to use for this wearable.
     * Return null to use the same as the primary model.
     */
    <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context);

    /**
     * Orients this wearable.
     */
    default void setLivingAnimations(IModel model, Entity entity) {

    }
}
