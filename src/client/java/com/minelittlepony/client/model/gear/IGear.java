package com.minelittlepony.client.model.gear;

import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IClientModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IPart;

import javax.annotation.Nonnull;
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
    boolean canRender(IClientModel model, Entity entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    /**
     * Gets the texture to use for this wearable.
     * Return null to use the same as the primary model.
     */
    @Nullable
    ResourceLocation getTexture(Entity entity);

    /**
     * Gets the actual body part this wearable will latch onto.
     */
    @Nonnull
    ModelRenderer getOriginBodyPart(AbstractPonyModel model);

    /**
     * Orients this wearable.
     */
    default void setLivingAnimations(IClientModel model, Entity entity) {

    }

    /**
     * Renders this wearable separately. (used outside of the gear render layer)
     */
    void renderSeparately(Entity entity, float scale);
}
