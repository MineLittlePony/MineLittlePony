package com.minelittlepony.client.model;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.IModelWrapper;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.mson.api.ModelKey;

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
        @SuppressWarnings("unchecked")
        M body = (M)key.createModel();
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
