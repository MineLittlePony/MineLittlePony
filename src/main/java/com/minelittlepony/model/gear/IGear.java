package com.minelittlepony.model.gear;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IGear extends IModelPart {

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
    void setLivingAnimations(IModel model, Entity entity);

    /**
     * Renders this wearable separately. (used outside of the gear render layer)
     */
    void renderSeparately(Entity entity, float scale);
}
