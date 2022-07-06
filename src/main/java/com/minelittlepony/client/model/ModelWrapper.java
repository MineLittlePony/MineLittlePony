package com.minelittlepony.client.model;

import net.minecraft.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.IModelWrapper;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.mson.api.ModelKey;

import java.util.function.Consumer;

/**
 * Container class for the various models and their associated piece of armour.
 */
public record ModelWrapper<T extends LivingEntity, M extends IModel> (
        M body,
        IArmour<?> armor
) implements IModelWrapper {
    /**
     * Creates a new model wrapper to contain the given pony.
     */
    public static <T extends LivingEntity, M extends IModel> ModelWrapper<T, M> of(ModelKey<?> key) {
        return of(key, null);
    }

    public static <T extends LivingEntity, M extends IModel> ModelWrapper<T, M> of(ModelKey<?> key, @Nullable Consumer<M> initializer) {
        @SuppressWarnings("unchecked")
        M body = (M)key.createModel();
        if (initializer != null) initializer.accept(body);
        IArmour<?> armor = body.createArmour();
        armor.applyMetadata(body.getMetadata());
        return new ModelWrapper<>(body, armor);
    }

    @Override
    public ModelWrapper<T, M> applyMetadata(IPonyData meta) {
        body.setMetadata(meta);
        armor.applyMetadata(meta);
        return this;
    }
}
