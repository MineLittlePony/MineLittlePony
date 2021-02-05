package com.minelittlepony.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.IModel;

import javax.annotation.Nullable;

/**
 * A render context for instance of IGear.
 *
 * @param <T> The type of entity being rendered.
 * @param <M> The type of the entity's primary model.
 */
public interface IRenderContext<T extends Entity, M extends IModel> {
    /**
     * The empty context.
     */
    IRenderContext<?, ?> NULL = (e, g) -> null;

    /**
     * Checks whether the given wearable and gear are able to render for this specific entity and its renderer.
     */
    default boolean shouldRender(M model, T entity, Wearable wearable, IGear gear) {
        return gear.canRender(model, entity);
    }

    @Nullable
    default M getEntityModel() {
        return null;
    }

    /**
     * Gets the default texture to use for this entity and wearable.
     *
     * May be the entity's own texture or a specific texture allocated for that wearable.
     */
    Identifier getDefaultTexture(T entity, Wearable wearable);
}
