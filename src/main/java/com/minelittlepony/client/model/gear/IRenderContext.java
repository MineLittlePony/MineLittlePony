package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IGear;

import javax.annotation.Nullable;

public interface IRenderContext<T extends Entity, M extends IModel> {

    IRenderContext<?, ?> NULL = (e, g) -> null;

    default boolean shouldRender(M model, T entity, Wearable wearable, IGear gear) {
        return gear.canRender(model, entity);
    }

    @Nullable
    default IModel getEntityModel() {
        return null;
    }

    Identifier getDefaultTexture(T entity, Wearable wearable);
}
