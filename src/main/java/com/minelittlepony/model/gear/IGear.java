package com.minelittlepony.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IPart;
import com.minelittlepony.model.IPonyModel;

import javax.annotation.Nullable;

public interface IGear extends IPart {

    /**
     * Determines if this wearable can and is worn by the selected entity.
     *
     * @param model     The primary model
     * @param entity    The entity being rendered
     *
     * @return True to render this wearable
     */
    boolean canRender(IPonyModel<?> model, Entity entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    /**
     * Gets the texture to use for this wearable.
     * Return null to use the same as the primary model.
     */
    @Nullable
    Identifier getTexture(Entity entity);

    /**
     * Orients this wearable.
     */
    default void setLivingAnimations(IPonyModel<?> model, Entity entity) {

    }

    /**
     * Renders this wearable separately. (used outside of the gear render layer)
     */
    void renderSeparately(Entity entity, float scale);
}
